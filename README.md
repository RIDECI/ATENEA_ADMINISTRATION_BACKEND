# 👨‍💼  ATENEA_ADMINISTRATION_BACKEND

Centraliza las herramientas de gestión y control del sistema,
permitiendo al área de movilidad o seguridad institucional supervisar la operación,
validar usuarios y emitir reportes.

## 👥 Developers
* Raquel Iveth Selma Alaya
* Nestor David Lopez Castañeda
* Juan Pablo Nieto Cortes
* Carlos David Astudillo Castiblanco
* Robinson Steven Nuñez Portela


## 🏛️ Project Architecture

The Nemesis - Geolocation, Routes and Tracking have a unacoplated hexagonal - clean architecture where looks for isolate the business logic with the other part of the app dividing it in multiple components:

* **🧠 Domain (Core)**: Contains the business logic and principal rules.

* **🎯 Ports (Interfaces)**: Are interfaces that define the actions that the domain can do.

* **🔌 Adapters (Infrastructure)**: Are the implementations of the ports that connect the domain with the specific technologies.

The use of this architecture has the following benefits:

* ✅ **Separation of Concerns:** Distinct boundaries between logic and infrastructure.
* ✅ **Maintainability:** Easier to update or replace specific components.
* ✅ **Scalability:** Components can evolve independently.
* ✅ **Testability:** The domain can be tested in isolation without a database or server.

## 📂 Clean - Hexagonal Structure

```
📂 nemesis_travel_management_backend
 ┣ 📂 src/
 ┃ ┣ 📂 main/
 ┃ ┃ ┣ 📂 java/
 ┃ ┃ ┃ ┗ 📂 edu/dosw/rideci/
 ┃ ┃ ┃   ┣ 📄 AteneaAdministrationBackEndApplication.java
 ┃ ┃ ┃   ┣ 📂 domain/
 ┃ ┃ ┃   ┃ ┗ 📂 model/            # 🧠 Domain models
 ┃ ┃ ┃   ┣ 📂 application/
 ┃ ┃ ┃   ┃ ┣ 📂 ports/
 ┃ ┃ ┃   ┃ ┃ ┣ 📂 input/          # 🎯 Input ports (Exposed use cases)
 ┃ ┃ ┃   ┃ ┃ ┗ 📂 output/         # 🔌 Output ports (external gateways)
 ┃ ┃ ┃   ┃ ┗ 📂 usecases/         # ⚙️ Use case implementations
 ┃ ┃ ┃   ┣ 📂 infrastructure/
 ┃ ┃ ┃   ┃ ┗ 📂 adapters/
 ┃ ┃ ┃   ┃   ┣ 📂 input/
 ┃ ┃ ┃   ┃   ┃ ┗ 📂 controller/   # 🌐 Input adapters (REST controllers)
 ┃ ┃ ┃   ┃   ┗ 📂 output/
 ┃ ┃ ┃   ┃     ┗ 📂 persistence/  # 🗄️ Output adapters (persistance)
 ┃ ┃ ┗ 📂 resources/
 ┃ ┃   ┗ 📄 application.properties
 ┣ 📂 test/
 ┃ ┣ 📂 java/
 ┃ ┃ ┗ 📂 edu/dosw/rideci/AteneaAdministrationBackEndApplication/
 ┃ ┃   ┗ 📄 AteneaAdministrationBackEndApplicationTests.java
 ┣ 📂 docs/
 ┃ ┣ diagramaClases.jpg
 ┃ ┣ diagramaDatos.jpg
 ┃ ┗ diagramaDespliegue.png
 ┣ 📄 pom.xml
 ┣ 📄 mvnw / mvnw.cmd
 ┗ 📄 README.md
```

# 📡 API Endpoints

For detailed documentation refer to our Swagger UI (Running locally at http://localhost:8080/swagger-ui.html).

Data input & output

| Method | URI | Description | Request Body / Params |
| :--- | :--- | :--- | :--- |
| `POST` | `/admin/reports` | Crear un nuevo reporte de seguridad | `SecurityReport` (JSON en el cuerpo) |
| `GET` | `/admin/reports` | Listar reportes de seguridad con filtros opcionales | Query Params: `type`, `from`, `to` (opcionales) |
| `GET` | `/admin/reports/export` | Exportar reportes a formato PDF, CSV o XLSX | Query Params: `type` (opcional), `format` (default: "xlsx") |
| `GET` | `/admin/drivers` | Listar conductores | Query Params: `status`, `search`, `page` (default: 0), `size` (default: 20) |
| `PATCH` | `/admin/drivers/{id}/approve` | Aprobar conductor | Path Variable: `id`, Query Param: `adminId` |
| `PATCH` | `/admin/drivers/{id}/reject` | Rechazar conductor | Path Variable: `id`, Request Body: `RejectDto` |
| `GET` | `/admin/drivers/{id}` | Ver detalles del conductor | Path Variable: `id` |
| `PATCH` | `/admin/drivers/{id}/documents-ref` | Agregar referencia de documento | Path Variable: `id`, Request Body: `DocumentRefDto`, Query Param: `uploadedBy` (opcional) |
| `POST` | `/admin/policies` | Crear una nueva política de publicación | Request Body: `PublicationPolicy` (JSON) |
| `PUT` | `/admin/policies/{id}` | Actualizar una política de publicación existente | Path Variable: `id`, Request Body: `PublicationPolicy` (JSON) |
| `GET` | `/admin/policies/{id}` | Obtener una política de publicación por su ID | Path Variable: `id` |
| `GET` | `/admin/policies` | Listar todas las políticas de publicación | - |
| `DELETE` | `/admin/policies/{id}` | Eliminar una política de publicación por su ID | Path Variable: `id` |
| `GET` | `/admin/policies/allowed` | Verificar si está permitido publicar en un momento específico | Query Params: `at`, `time`, `userId`, `role` (opcionales) |
| `GET` | `/admin/trips` | Listar viajes | Query Params: `search`, `status`, `type`, `page` (default: 0), `size` (default: 20) |
| `GET` | `/admin/trips/active` | Obtener viajes activos | - |
| `GET` | `/admin/trips/{id}` | Obtener detalle de un viaje | Path Variable: `id` |
| `GET` | `/admin/trips/metrics` | Obtener métricas del dashboard | - |
| `GET` | `/admin/users` | Listar usuarios, filtros opcionales | Query Params: `search`, `status`, `role`, `page` (default: 0), `size` (default: 20) |
| `GET` | `/admin/users/{id}` | Obtener detalle de un usuario | Path Variable: `id` |
| `PATCH` | `/admin/users/{id}/suspend` | Suspender usuario | Path Variable: `id`, Request Body: `SuspendUserRequestDto` |
| `PATCH` | `/admin/users/{id}/activate` | Activar usuario | Path Variable: `id`, Query Param: `adminId` |
| `PATCH` | `/admin/users/{id}/block` | Bloquear usuario | Path Variable: `id`, Query Param: `adminId`, Request Body: `reason` (opcional) |

### 📟 HTTP Status Codes
Common status codes returned by the API.

| Code | Status | Description |
| :--- | :--- | :--- |
| `200` | **OK** | Request processed successfully. |
| `201` | **Created** | Resource (Route/Tracking) created successfully. |
| `400` | **Bad Request** | Invalid coordinates or missing parameters. |
| `401` | **Unauthorized** | Missing or invalid JWT token. |
| `404` | **Not Found** | Route or Trip ID does not exist. |
| `500` | **Internal Server Error** | Unexpected error (e.g., Google Maps API failure).

# Input and Output Data

Data information per functionability


# 🔗 Connections with other Microservices

This module does not work alone. It interacts with the RideCi Ecosystem via REST APIs and Message Brokers:

1. Travel Management Module: Receives information about the travel.

# Technologies

The following technologies were used to build and deploy this module:

### Backend & Core
![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apache-maven&logoColor=white)

### Database
![MongoDB](https://img.shields.io/badge/MongoDB-%234ea94b.svg?style=for-the-badge&logo=mongodb&logoColor=white)

### DevOps & Infrastructure
![Docker](https://img.shields.io/badge/docker-%230db7ed.svg?style=for-the-badge&logo=docker&logoColor=white)
![Kubernetes](https://img.shields.io/badge/kubernetes-%23326ce5.svg?style=for-the-badge&logo=kubernetes&logoColor=white)
![Railway](https://img.shields.io/badge/Railway-131415?style=for-the-badge&logo=railway&logoColor=white)
![Vercel](https://img.shields.io/badge/vercel-%23000000.svg?style=for-the-badge&logo=vercel&logoColor=white)

### CI/CD & Quality Assurance
![GitHub Actions](https://img.shields.io/badge/github%20actions-%232671E5.svg?style=for-the-badge&logo=githubactions&logoColor=white)
![SonarQube](https://img.shields.io/badge/SonarQube-4E9BCD?style=for-the-badge&logo=sonarqube&logoColor=white)
![JaCoCo](https://img.shields.io/badge/JaCoCo-Coverage-green?style=for-the-badge)

### Documentation & Testing
![Swagger](https://img.shields.io/badge/-Swagger-%23Clojure?style=for-the-badge&logo=swagger&logoColor=white)
![Postman](https://img.shields.io/badge/Postman-FF6C37?style=for-the-badge&logo=postman&logoColor=white)

### Design
![Figma](https://img.shields.io/badge/figma-%23F24E1E.svg?style=for-the-badge&logo=figma&logoColor=white)

### Comunication & Project Management
![Jira](https://img.shields.io/badge/jira-%230A0FFF.svg?style=for-the-badge&logo=jira&logoColor=white)
![Slack](https://img.shields.io/badge/Slack-4A154B?style=for-the-badge&logo=slack&logoColor=white)
---

# 🌿 Branches Strategy & Structure

This module follows a strict branching strategy based on Gitflow to ensure the ordered versioning,code quality and continous integration.



| **Branch**                | **Purpose**                            | **Receive of**           | **Sent to**        | **Notes**                      |
| ----------------------- | ---------------------------------------- | ----------------------- | ------------------ | ------------------------------ |
| `main`                  | 🏁 Stable code for preproduction or Production | `release/*`, `hotfix/*` | 🚀 Production      | 🔐 Protected with PR y successful CI   |
| `develop`               | 🧪 Main developing branch             | `feature/*`             | `release/*`        | 🔄 Base to continous deployment |
| `feature/*`             | ✨ New functions or refactors  to be implemented       | `develop`               | `develop`          | 🧹 Are deleted after merge to develop      |
| `release/*`             | 📦 Release preparation & final polish.      | `develop`               | `main` y `develop` | 🧪  Includes final QA. No new features added here.     |
| `bugfix/*` o `hotfix/*` | 🛠️ Critical fixes for production         | `main`                  | `main` y `develop` | ⚡ Urgent patches. Highest priority             |


# 🏷️ Naming Conventions

## 🌿 Branch Naming

### ✨ Feature Branches
Used for new features or non-critical improvements.

**Format:**
`feature/[shortDescription]`

**Examples:**
- `feature/authenticationModule`
- `feature/securityService`

**Rules:**
* 🧩 **Case:** strictly *camelCase* (lowercase with hyphens).
* ✍️ **Descriptive:** Short and meaningful description.
---

### 📦 Release Branches
Used for preparing a new production release. Follows [Semantic Versioning](https://semver.org/).

**Format:**
`release/v[major].[minor].[patch]`

**Examples:**
- `release/v1.0.0`
- `release/v1.1.0-beta`

---

### 🚑 Hotfix Branches
Used for urgent fixes in the production environment.

**Format:**
`hotfix/[shortDescription]`

**Examples:**
- `hotfix/fixTokenExpiration`
- `hotfix/securityPatch`

---

## 📝 Commit Message Guidelines

We follow the **[Conventional Commits](https://www.conventionalcommits.org/)** specification.

### 🧱 Standard Format

```text
<type>(<scope>): <short description>
```

# 📐 System Architecture & Design

This section provides a visual representation of the module's architecture ilustrating the base diagrams to show the application structure and components flow.


### 🧩 Context Diagram

---

El Módulo de Administración Institucional actúa como el centro de supervisión integral de RIDECI,
donde los administradores gestionan y controlan todos los aspectos críticos de la plataforma para
garantizar seguridad y eficiencia.

Sus funciones principales incluyen:

- Validación de conductores y vehículos mediante revisiones documentales

- Monitoreo de seguridad con revisión de reportes y gestión de incidentes

- Control de usuarios mediante suspensiones temporales por incumplimientos

- Regulación operativa definiendo horarios permitidos para viajes

- Análisis de datos mediante estadísticas de uso y generación de reportes PDF

![Context Diagram](docs/uml/DiagramaContexto.png)



### 🧩 Specific Components Diagram

---


El módulo de Administración Institucional usa Arquitectura Hexagonal para mantener la lógica de negocio
independiente de frameworks y detalles técnicos. Esto facilita pruebas, actualizaciones y despliegues ágiles.

#### Estructura y flujo

El frontend en React y TypeScript llama controladores que invocan casos de uso. Los casos de uso contienen la lógica central: aprobación de conductores, suspensión de usuarios y generación de reportes. Los casos de uso sólo dependen de puertos, manteniendo el core aislado.

#### Puertos y adaptadores

Los puertos definen contratos para persistencia, publicación de eventos y notificaciones. Los adaptadores implementan esos contratos integrando con MongoDB, RabbitMQ y servicios externos de autenticación y reputación. Esto permite sustituir o simular implementaciones en pruebas.

#### Auditoría y eventos

Todas las acciones administrativas se registran en auditoría y se propagan como eventos con identificadores de correlación y comandos para idempotencia y trazabilidad. El procesamiento asíncrono evita bloquear la operación principal.

#### Políticas y extensibilidad

Las políticas de publicación se evalúan con un factory de estrategias. El patrón strategy permite añadir reglas como días permitidos, roles o excepciones sin tocar el core y facilita pruebas unitarias de cada regla.

#### Ejemplo de flujo

Al aprobar un conductor el flujo va del frontend al caso de uso, que actualiza el repositorio, registra la acción en auditoría y publica un evento. Listeners consumen el evento para notificaciones, actualizaciones de reputación o generación de reportes sin impactar la operación inicial.


### Funcionamiento del Módulo de Administrador Institucional:

#### Gestión de usuarios

- Listar usuarios y ver sus detalles.

- Suspender, activar o bloquear usuarios.

- Gestionar perfiles de conductores, aprobando o rechazando según los documentos enviados para validar sus papeles.

#### Seguimiento de viajes

- Realizar seguimiento a los viajes en tiempo real para obtener información y tomar
  acciones si ocurre algo sospechoso con un usuario.

#### Políticas de publicación

- Configurar políticas para publicar horarios de trabajo los días de la semana y en horas específicas,
  de modo que los conductores trabajen siguiendo ese horario.

#### Reportes

- Recibir reportes de seguridad.

- Exportar reportes a CSV, Excel o PDF según sea necesario.


#### Métricas y estadísticas

- Recibir métricas y estadísticas para contemplar el panorama ambiental y sostenible.


#### Restricciones de negocio:

- El administrador institucional se encargará de configurar los horarios, permitiendo que los conductores solo puedan laborar de lunes a sábado durante todo el día.

- Al suspender un usuario, este no se activará automáticamente después de un tiempo; el administrador debe activarlo manualmente.

- En caso de suspenderlo, se cambiará su rol por uno que esté activo.


![Specific Components Diagram](docs/uml/DiagramaComponentesEspecifico.png)


### 🧩 Use Cases Diagram

---

Administración Institucional en RIDECI permite a los administradores validar cuentas de conductores y vehículos,
visualizar viajes activos y sus participantes, configurar horarios permitidos para viajes,
exportar reportes en formatos como PDF, revisar estadísticas de uso y datos de sostenibilidad,
así como analizar reportes de seguridad y comportamiento de usuarios.

Este módulo funciona como el centro de control del sistema, garantizando el cumplimiento de políticas
institucionales y manteniendo la seguridad mediante la supervisión constante de todas las operaciones,
mientras proporciona herramientas completas de gestión y generación de informes para la toma de decisiones institucionales.


![Use Cases Diagram](docs/uml/DiagramaCasosUso.png)


#### 🧩 Class Diagram

---

### Patrones de diseño:

#### Strategy:

Se uso ya que nos permite encapsular las reglas de las políticas de publicación de RidECI
y poder intercambiarlas y combinarlas sin necesidad de cambiar al cliente,
en este caso los conductores que tienen que seguir el horario establecido con las horas.

#### Composite:

Se uso junto al patron de diseño strategy ya que agrupa todas las políticas permitiendo evaluarlas
y facilitar si se quieren añadir más reglas compuestas.


#### Factory:

Trabaja en conjunto con Composite y nos permitio evitar centralizar toda la logica de
la politica de los horarios ya que define criterios definidos basados
en una política  como ser validar el rol, hasta que horas un conductor debe trabajar y que días de la semana.


#### Command

No se ve reflejado en el diagrama de clases pero se uso para los eventos ya que modela una accion la cual tenemos que
consumir para que sea ejecutado y sirva como por ejemplo con los eventos de inicio y fin de un viaje para
que el administrador pueda actuar según la situación.

---

### **Principios SOLID:**

#### **Single Responsability:**

- User para centralizar la logica de los roles de los usuarios y poder manejar su perfil segun su comportamiento.

- Driver el condutor que quiere validar su cuenta, el cual el admin debe revisar y determinar si sus papeles ameritan que sea conductor.

- Trip Monitor para que el administrador esta atento a los viajes y determinar que no se salga de su ruta o algun movimiento
  raro por parte del conductor.

- Security report manejea los reportes realizados por los usuarios y que el administrador pueda mantener bajo control
  cualquier situación.

- Export Report para que el adminitrador si se requiere un documento por cvs, pdf o excel puedo exportar el reporte y
  entregarlo para investigación o evidencia si se requiere.

- AdminAction guarda las auditorias es decir las acciones que un administrador llevo a cabo ya sea sobre suspender o validar
  un usuario o conductor.

- Publicación de politicas y strategy nos permite manejar distintas politicas y que sean faciles de agregar en la aplicación.


#### Open/Closed:

Podemos extender las politicas de publicacion para incluir a mas de un tipo de estas por lo que cada politica funciona de
manera independiente sin centralizar toda la logica en una sola clase.


#### Interface Segregation Principle:

Las implementaciones de PolicyStrategy son intercambiables nadie necesita conocer la implementación concreta


![Class Diagram](docs/uml/DiagramaClases.png)


### 🧩 Data Base Diagram

---

La base de datos usa mayormente documentos referenciados para mantener consistencia, rendimiento y escalabilidad.

AdminAction se almacena embebido porque se accede habitualmente junto al recurso afectado y se requiere atomicidad en lecturas rápidas.

MongoDB se eligió por su modelo documental flexible, escalabilidad y buena integración con el stack.

Ademas nos permite manejar documentos de forma embebida y referenciada y no es tan estricto, ya que ofrece un integración fácil en repositorios y mapping.

Utiliza MongoDB para almacenar datos institucionales.


#### Documentos Referenciados:

- Evita duplicación de datos y mantiene la consistencia cuando las entidades se usan en muchos contextos.

- Permite paginación y manejo eficiente de colecciones que crecen mucho como viajes y reportes.

- Facilita actualizaciones independientes sin reescribir grandes documentos padre.

- Suma flexibilidad para consultas y agregaciones usando lookup solo cuando se necesita.



![DiagramaBasesDatos](docs/uml/DiagramaBaseDeDatos.png)


### 🧩 Sequence Diagrams

---

Los diagramas de secuencias estan enfocados en seguir la estructura limpia del proyecto siguiendo el el siguiente flujo:

- Controller
- Use Case
- Repository Port
- Repository Adapter
- Mongo Repository 

Luego usa la base de datos Mongo para evidenciar los documentos 



📄 [Ver diagrama de secuencia](docs/pdf/diagramaSecuencias.pdf)



### 🧩 Specific Deploy Diagram

---

#### Backend y Despliegue

- Desarrollado en Java con Spring Boot.

- Desplegado automáticamente en Railway mediante un pipeline de CI/CD con GitHub Actions.

#### Base de Datos

- Usa MongoDB para almacenar datos de:

    - Validaciones de usuarios.

    - Registros de auditoría.

    - Reportes institucionales.

#### Calidad del Código

- Integra JaCoCo para medir cobertura de pruebas.

- Utiliza SonarQube para análisis estático y detección de vulnerabilidades.

#### Funcionalidades Principales

- Supervisión de viajes.

- Validación segura de accesos y registros.

- Generación de reportes institucionales.


![Specific Deploy Diagram](docs/uml/DiagramaDespliegue.png)


### 🧩 General Component Diagram

---


#### **Frontend:** 
 
Desarrollado en TypeScript y desplegado en Vercel".


#### **API Gateway:** 

Centraliza y gestiona las comunicaciones entre los componentes.


#### **Backend:** 

Gestiona la lógica de administración institucional, integrando JaCoco SonarQube para garantizar calidad de código y funcione de manera correcta para los conductores, viajes y usuarios.

Ademas usamos un Pipeline para validar que todo funcione como debe funcionar.

Desplieguemos en Railway para construir el Docker, usamos Swagger y PostMan para probar y spring boot para gestionar el proyecto de manera eficiente mediante una API REST flexible.


![alt text](docs/uml/DiagramaComponentesGeneral.png)

# 🚀 Getting Started

### Requesitos
- Java 17
- Maven 3.X
- Docker + Docker Compose
- Puerto disponiblo 8080

### Clone & open repository

`git clone https://github.com/RIDECI/ATENEA_ADMINISTRATION_BACKEND.git`

`cd ATENEA_ADMINISTRATION_BACKEND`

### Dockerize the project

Dockerize before compile the project avoid configuration issues and ensure environment consistency.

``` bash
docker compose up -d
```

[Ver video demostrativo](https://youtu.be/3EqpeV_jBLM)

### Install dependencies & compile project

Download dependencies and compile the source code.

``` bash
mvn clean install
```

``` bash
mvn clean compile
```

### To run the project
Start the Spring Boot server

``` bash
mvn spring-boot:run
```

--- 

#### Prueba de Ejecución Local:

[Ver video demostrativo](https://youtu.be/waTVMDQHkIA)



---
# 🧪 Testing

Testing is a essential part of the project functionability, this part will show the code coverage and code quality analazing with tools like JaCoCo and SonarQube.

### 📊 Code Coverage (JaCoCo)

---

[Ver video de cobertura y jacoco](https://youtu.be/EU2rkBedgvs)

![JaCoCo](docs/imagenes/jacoco1.png)


![JaCoCo](docs/imagenes/jacoco2.png)


![JaCoCo](docs/imagenes/jacoco3.png)





### 🔍 Static Analysis (SonarQube)

---

[Ver video de cobertura de sonar](https://youtu.be/Gdg_f3UIo0c)

![SonarQube](docs/imagenes/sonar.png)

![SonarQube](docs/imagenes/sonar2.png)

![SonarQube](docs/imagenes/sonar3.png)


### 💻  Evidence Swagger

---

![EvidenciaSwagger](docs/imagenes/swagger.png)

**RIDECI** - Conectando a la comunidad para moverse de forma segura, económica y sostenible.