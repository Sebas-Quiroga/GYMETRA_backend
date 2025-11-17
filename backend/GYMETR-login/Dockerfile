###############################
# Etapa 1: Resolución de dependencias
###############################
FROM eclipse-temurin:17-jdk-alpine AS deps
WORKDIR /workspace
# ARG para inyectar metadata
ARG BUILD_TIME
ARG GIT_COMMIT

# Copiamos sólo lo necesario para cachear dependencias
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw mvnw
COPY mvnw.cmd mvnw.cmd

# Descargamos dependencias (sin código fuente aún) para maximizar cache
RUN --mount=type=cache,target=/root/.m2 \
		./mvnw -q -B dependency:go-offline

###############################
# Etapa 2: Build
###############################
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /workspace

# Reutilizamos el cache de dependencias (no copiamos /root/.m2 porque el mount no persiste en la capa)
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw mvnw
COPY mvnw.cmd mvnw.cmd

# Copiamos el código fuente completo (invalidará cache sólo cuando cambia src)
COPY src src

ARG SKIP_TESTS=true
ENV MAVEN_SKIP_TESTS=${SKIP_TESTS}

RUN --mount=type=cache,target=/root/.m2 \
    if [ "$MAVEN_SKIP_TESTS" = "true" ]; then \
        ./mvnw -q -DskipTests clean package; \
    else \
        ./mvnw -q clean package; \
    fi

###############################
# Etapa 3: Runtime
###############################
FROM eclipse-temurin:17-jre-alpine AS runtime
WORKDIR /app

# Crear usuario no-root seguro
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

ARG GIT_COMMIT
ARG BUILD_TIME

LABEL org.opencontainers.image.title="gymetra-backend" \
			org.opencontainers.image.source="https://github.com/Sebas-Quiroga/GYMETRA-V1" \
			org.opencontainers.image.revision="$GIT_COMMIT" \
			org.opencontainers.image.created="$BUILD_TIME" \
			org.opencontainers.image.description="Backend GYMETRA (Login)"

COPY --from=build /workspace/target/*.jar app.jar

EXPOSE 8080

ENV SPRING_PROFILES_ACTIVE=prod \
		JAVA_OPTS="-Xmx512m -Xms256m"

HEALTHCHECK --interval=30s --timeout=5s --start-period=40s --retries=3 CMD wget -qO- http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]