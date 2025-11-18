pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                checkout scm

                script {
                    // Obtener la rama desde Jenkins
                    def branchName = env.BRANCH_NAME ?: "repositorio"
                    def safeBranch = branchName.replace('/', '-')
                    env.SAFE_BRANCH = safeBranch

                    echo "Rama detectada: ${branchName}"
                    echo "SAFE_BRANCH: ${env.SAFE_BRANCH}"
                }
            }
        }

        stage('Deploy with Docker Compose') {
            steps {
                dir('backend') {
                    bat '''
                        echo === DEPLOY BACKEND DOCKER COMPOSE ===
                        docker compose ps > status.txt 2>&1

                        findstr /C:"Up" status.txt >nul
                        if %ERRORLEVEL%==0 (
                            echo Servicios ya están arriba. No se recrean.
                        ) else (
                            echo Levantando servicios con build...
                            docker compose up -d --build
                        )

                        echo Deployment backend completado.
                    '''
                }
            }
        }
    }

    post {
        always {
            echo "Pipeline finalizado."
        }
        failure {
            echo "El pipeline falló."
        }
    }
}