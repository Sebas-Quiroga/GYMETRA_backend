# GYMETRA Backend

🏋️ **Gym Membership Management System - Backend**

📋 **Descripción**  
El backend de **GYMETRA** es un sistema distribuido para la gestión integral de membresías de gimnasio. Desarrollado como proyecto académico del curso de **Sistemas Distribuidos**, implementado con **Spring Boot 3.x** y **PostgreSQL**.  
Incluye autenticación, manejo de usuarios, membresías, pagos, control de acceso con QR y reportes.

---

## ✨ Características Clave

- 🔐 **Autenticación y Autorización** con JWT  
- 👥 **Gestión de Usuarios** (registro, login, perfiles)  
- 💳 **Gestión de Membresías** (registro, renovación, suspensión)  
- 💰 **Procesamiento de Pagos** integrado  
- 📱 **Control de Acceso con QR** en tiempo real  
- 📊 **Reportes y Análisis** de asistencia e ingresos  
- 🏗️ **Arquitectura distribuida basada en microservicios**

---

## 🏗️ Arquitectura del Sistema

### Microservicios Principales
- **Auth & Users Service**: inicio de sesión, roles, seguridad  
- **Memberships Service**: planes y membresías  
- **Payments Service**: pagos y conciliación  
- **Access Control Service**: validación QR y registro de acceso  
- **Reports Service**: analítica e informes  
- **API Gateway**: enrutamiento y políticas cross-cutting  

### Tecnologías

**Backend**
- Spring Boot 3.x  
- Spring Security + JWT  
- PostgreSQL  
- RabbitMQ / Kafka  
- OpenAPI - Swagger  

**Frontend**
- Vue.js 3  
- Ionic + Vue  
- Pinia / Vuex  

**Infraestructura**
- Docker  
- Docker Compose  
- CI/CD con Jenkins  

---

## 🔧 Instalación y Configuración

### 1. Requisitos Previos
- Java 17+  
- PostgreSQL 14+  
- Node.js 18+  
- Git  

### 2. Clonar el Repositorio
```bash
git clone https://github.com/Sebas-Quiroga/GYMETRA_backend.git
cd GYMETRA_backend
