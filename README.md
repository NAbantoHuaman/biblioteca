# 📚 API de Biblioteca Universitaria

> **API RESTful** para la gestión integral de bibliotecas universitarias, desarrollada con **Spring Boot 3.5.3** y **Java 21**.

## 📋 Tabla de Contenidos

- [Descripción del Sistema](#-descripción-del-sistema)
- [Tecnologías Utilizadas](#-tecnologías-utilizadas)
- [Arquitectura del Proyecto](#-arquitectura-del-proyecto)
- [Diagrama de Entidades](#-diagrama-de-entidades)
- [Requisitos Previos](#-requisitos-previos)
- [Instalación y Ejecución](#-instalación-y-ejecución)
- [Configuración de Base de Datos](#-configuración-de-base-de-datos)
- [Endpoints de la API](#-endpoints-de-la-api)
- [Autenticación JWT](#-autenticación-jwt)
- [Ejemplos de Uso](#-ejemplos-de-uso)
- [Documentación Swagger](#-documentación-swagger)
- [Autores](#-autores)

---

## 📖 Descripción del Sistema

Sistema backend completo para la administración de bibliotecas universitarias que permite:

- **📖 Gestión de libros**: CRUD completo del catálogo (crear, leer, actualizar, eliminar).
- **👤 Gestión de usuarios**: Registro, autenticación y administración de cuentas.
- **📋 Préstamos y devoluciones**: Control completo del flujo de préstamo de libros.
- **🔐 Seguridad JWT**: Autenticación sin estado con tokens de acceso y refresco.
- **🛡️ Control de acceso por roles**: Permisos diferenciados para ADMIN y USUARIO.
- **✅ Validación de datos**: Validaciones robustas en todas las entradas.
- **🚨 Manejo centralizado de errores**: Respuestas de error consistentes en JSON.

---

## 🛠 Tecnologías Utilizadas

| Tecnología | Versión | Propósito |
|:---|:---|:---|
| **Java** | 21 (LTS) | Lenguaje de programación |
| **Spring Boot** | 3.5.3 | Framework backend |
| **Spring Security** | 6.x | Seguridad y autenticación |
| **Spring Data JPA** | - | Persistencia de datos |
| **Hibernate** | 6.x | ORM (mapeo objeto-relacional) |
| **PostgreSQL** | 15.x | Base de datos relacional |
| **JWT (jjwt)** | 0.12.6 | Tokens de autenticación |
| **Swagger/OpenAPI** | 2.8.6 | Documentación de la API |
| **Lombok** | - | Reducción de código boilerplate |
| **Maven** | - | Gestión de dependencias |

---

## 🏗 Arquitectura del Proyecto

El proyecto sigue una **arquitectura en capas** con separación clara de responsabilidades:

```
src/main/java/pe/edu/idat/biblioteca/
├── config/              # Configuración (Security, OpenAPI, CORS)
├── controller/          # Controladores REST (endpoints)
├── dto/
│   ├── request/         # DTOs de entrada (validaciones)
│   └── response/        # DTOs de salida (sin datos sensibles)
├── entity/              # Entidades JPA (mapeo a tablas)
├── enums/               # Enumeraciones (RolNombre, EstadoPrestamo)
├── exception/           # Excepciones personalizadas + Handler global
├── mapper/              # Conversión Entity ↔ DTO
├── repository/          # Repositorios Spring Data JPA
├── security/            # JWT (Service, Filter, EntryPoint)
└── service/
    └── impl/            # Servicios (interface + implementación)
```

---

## 🗂 Diagrama de Entidades

```
┌──────────────┐       ┌──────────────────┐       ┌──────────────┐
│   USUARIO    │       │   USUARIO_ROLES  │       │     ROL      │
├──────────────┤       │   (tabla interm.) │       ├──────────────┤
│ id (PK)      │───┐   ├──────────────────┤   ┌───│ id (PK)      │
│ nombre       │   └──>│ usuario_id (FK)  │   │   │ nombre (UK)  │
│ apellido     │       │ rol_id (FK)      │<──┘   │  (ADMIN /    │
│ email (UK)   │       └──────────────────┘       │   USUARIO)   │
│ password     │                                  └──────────────┘
│ activo       │
│ fecha_reg    │
└──────┬───────┘
       │ 1:N
       ▼
┌──────────────┐       ┌──────────────────┐
│  PRESTAMO    │       │     LIBRO        │
├──────────────┤       ├──────────────────┤
│ id (PK)      │       │ id (PK)          │
│ usuario_id   │───┐   │ titulo           │
│ libro_id     │───┼──>│ autor            │
│ fecha_prest  │   │   │ isbn (UK)        │
│ fecha_dev_e  │   │   │ editorial        │
│ fecha_dev_r  │   │   │ anio_publicacion │
│ estado       │   │   │ genero           │
└──────────────┘   │   │ stock            │
                   │   │ disponible       │
                   │   │ fecha_reg        │
                   │   └──────────────────┘
                   │           1:N ▲
                   └───────────────┘

┌──────────────────┐
│  REFRESH_TOKEN   │
├──────────────────┤
│ id (PK)          │
│ token (UK)       │
│ usuario_id (FK)  │──> USUARIO
│ fecha_expiracion │
└──────────────────┘
```

### Relaciones

| Relación | Tipo | Descripción |
|:---|:---|:---|
| Usuario ↔ Rol | **ManyToMany** | Un usuario puede tener múltiples roles |
| Usuario → Préstamo | **OneToMany** | Un usuario puede tener múltiples préstamos |
| Libro → Préstamo | **OneToMany** | Un libro puede estar en múltiples préstamos |
| Usuario → RefreshToken | **OneToOne** | Un usuario tiene un refresh token activo |

---

## 📋 Requisitos Previos

Antes de ejecutar el proyecto, asegúrate de tener instalado:

- **Java 21** (JDK) — [Descargar](https://www.oracle.com/java/technologies/downloads/)
- **Maven 3.9+** — [Descargar](https://maven.apache.org/download.cgi)
- **PostgreSQL 15+** — [Descargar](https://www.postgresql.org/download/)

### Verificar instalaciones

```bash
java -version     # java version "21.x.x"
mvn -version      # Apache Maven 3.9.x
psql --version    # psql (PostgreSQL) 15.x
```

---

## 🚀 Instalación y Ejecución

### 1. Clonar el repositorio

```bash
git clone https://github.com/tu-usuario/biblioteca-api.git
cd biblioteca-api
```

### 2. Crear la base de datos

```sql
CREATE DATABASE IF NOT EXISTS biblioteca_db;
```

> **Nota**: Las tablas se crean automáticamente gracias a `spring.jpa.hibernate.ddl-auto=update`.

### 3. Configurar credenciales de PostgreSQL

Editar el archivo `src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/biblioteca_db
    username: postgres    # ← Tu usuario de PostgreSQL
    password: root        # ← Tu contraseña de PostgreSQL
```

### 4. Compilar y ejecutar

```bash
# Compilar el proyecto
mvn clean compile

# Ejecutar la aplicación
mvn spring-boot:run
```

La aplicación se iniciará en **http://localhost:8080**.

### 5. Acceder a Swagger UI

Abrir en el navegador: **http://localhost:8080/swagger-ui.html**

---

## 🔑 Configuración de Base de Datos

La aplicación crea automáticamente al iniciar:

| Dato | Descripción |
|:---|:---|
| Rol `ADMIN` | Rol de administrador |
| Rol `USUARIO` | Rol de usuario regular |
| Usuario admin | `admin@biblioteca.edu.pe` / `Admin123!` |
| 5 libros de ejemplo | Cien años de soledad, Don Quijote, El principito, etc. |

---

## 📡 Endpoints de la API

### 🔓 Autenticación (`/api/auth`)

| Método | Ruta | Acceso | Descripción |
|:---:|:---|:---|:---|
| `POST` | `/api/auth/register` | 🌐 Público | Registro de nuevo usuario |
| `POST` | `/api/auth/login` | 🌐 Público | Inicio de sesión → JWT |
| `POST` | `/api/auth/refresh` | 🌐 Público | Renovar access token |
| `POST` | `/api/auth/logout` | 🔒 Autenticado | Cerrar sesión |

### 📖 Libros (`/api/libros`)

| Método | Ruta | Acceso | Descripción |
|:---:|:---|:---|:---|
| `GET` | `/api/libros` | 🌐 Público | Listar todos los libros |
| `GET` | `/api/libros/{id}` | 🌐 Público | Obtener libro por ID |
| `GET` | `/api/libros/buscar?termino=X` | 🌐 Público | Buscar por título/autor |
| `GET` | `/api/libros/disponibles` | 🌐 Público | Libros con stock |
| `GET` | `/api/libros/genero/{genero}` | 🌐 Público | Filtrar por género |
| `POST` | `/api/libros` | 🔴 ADMIN | Crear libro |
| `PUT` | `/api/libros/{id}` | 🔴 ADMIN | Actualizar libro |
| `DELETE` | `/api/libros/{id}` | 🔴 ADMIN | Eliminar libro |

### 📋 Préstamos (`/api/prestamos`)

| Método | Ruta | Acceso | Descripción |
|:---:|:---|:---|:---|
| `POST` | `/api/prestamos` | 🔴 ADMIN | Registrar préstamo |
| `PUT` | `/api/prestamos/devolver/{id}` | 🔒 Autenticado | Registrar devolución |
| `GET` | `/api/prestamos/mis-prestamos` | 🔒 Autenticado | Mi historial |
| `GET` | `/api/prestamos/todos` | 🔴 ADMIN | Todos los préstamos |
| `GET` | `/api/prestamos/vencidos` | 🔴 ADMIN | Préstamos vencidos |

### 👤 Usuarios (`/api/usuarios`)

| Método | Ruta | Acceso | Descripción |
|:---:|:---|:---|:---|
| `GET` | `/api/usuarios/perfil` | 🔒 Autenticado | Ver mi perfil |
| `GET` | `/api/usuarios` | 🔴 ADMIN | Listar usuarios |
| `GET` | `/api/usuarios/{id}` | 🔴 ADMIN | Obtener usuario por ID |
| `PUT` | `/api/usuarios/{id}/estado?activo=true` | 🔴 ADMIN | Activar/desactivar |

**Leyenda**: 🌐 Público | 🔒 Autenticado | 🔴 Solo ADMIN

---

## 🔐 Autenticación JWT

El sistema utiliza **JSON Web Tokens (JWT)** para autenticación sin estado:

1. **Access Token**: Validez de 15 minutos. Se envía en cada petición protegida.
2. **Refresh Token**: Validez de 7 días. Permite obtener un nuevo access token sin re-login.

### Flujo de autenticación

```
1. POST /api/auth/login  →  { accessToken, refreshToken }
2. GET /api/libros (Header: Authorization: Bearer <accessToken>)
3. Si el accessToken expira:
   POST /api/auth/refresh { refreshToken }  →  { nuevo accessToken, nuevo refreshToken }
4. POST /api/auth/logout  →  Invalida refresh tokens
```

---

## 💡 Ejemplos de Uso

### Registro de usuario

```bash
POST /api/auth/register
Content-Type: application/json

{
    "nombre": "Juan",
    "apellido": "Pérez",
    "email": "juan@universidad.edu.pe",
    "password": "MiPassword123"
}
```

**Respuesta** (201 Created):
```json
{
    "accessToken": "eyJhbGciOiJIUzI1NiIs...",
    "refreshToken": "550e8400-e29b-41d4-a716-446655440000",
    "tokenType": "Bearer",
    "email": "juan@universidad.edu.pe",
    "nombreCompleto": "Juan Pérez",
    "roles": ["USUARIO"]
}
```

### Login

```bash
POST /api/auth/login
Content-Type: application/json

{
    "email": "admin@biblioteca.edu.pe",
    "password": "Admin123!"
}
```

### Crear libro (ADMIN)

```bash
POST /api/libros
Authorization: Bearer <accessToken>
Content-Type: application/json

{
    "titulo": "Clean Code",
    "autor": "Robert C. Martin",
    "isbn": "978-0132350884",
    "editorial": "Prentice Hall",
    "anioPublicacion": 2008,
    "genero": "Ingeniería de Software",
    "stock": 3
}
```

**Respuesta** (201 Created):
```json
{
    "id": 6,
    "titulo": "Clean Code",
    "autor": "Robert C. Martin",
    "isbn": "978-0132350884",
    "editorial": "Prentice Hall",
    "anioPublicacion": 2008,
    "genero": "Ingeniería de Software",
    "stock": 3,
    "disponible": true,
    "fechaRegistro": "2026-04-04T17:00:00"
}
```

### Registrar préstamo (ADMIN)

```bash
POST /api/prestamos
Authorization: Bearer <accessToken>
Content-Type: application/json

{
    "libroId": 1,
    "usuarioId": 2,
    "diasPrestamo": 14
}
```

### Registrar devolución

```bash
PUT /api/prestamos/devolver/1
Authorization: Bearer <accessToken>
```

### Buscar libros

```bash
GET /api/libros/buscar?termino=García
```

### Error de validación (ejemplo)

```json
{
    "timestamp": "2026-04-04 17:30:00",
    "status": 422,
    "error": "Error de Validación",
    "mensaje": "Los datos enviados contienen errores de validación",
    "path": "/api/auth/register",
    "erroresValidacion": {
        "email": "El email debe tener un formato válido",
        "password": "La contraseña debe tener entre 6 y 100 caracteres"
    }
}
```

---

## 📄 Documentación Swagger

La documentación interactiva de la API está disponible en:

| Recurso | URL |
|:---|:---|
| **Swagger UI** | http://localhost:8080/swagger-ui.html |
| **OpenAPI JSON** | http://localhost:8080/v3/api-docs |

Para probar endpoints protegidos en Swagger:
1. Ejecutar `/api/auth/login` con las credenciales
2. Copiar el `accessToken` de la respuesta
3. Hacer clic en **"Authorize"** (🔒)
4. Pegar el token y hacer clic en **"Authorize"**
5. Ahora puedes probar los endpoints protegidos

---

## 👥 Autores

| Integrante | Rol |
|:---|:---|
| Integrante 1 | Desarrollo Backend |
| Integrante 2 | Desarrollo Backend |
| Integrante 3 | Desarrollo Backend |

**Institución**: IDAT — Escuela de Tecnología

---

## 📝 Licencia

Este proyecto fue desarrollado como trabajo académico para el curso de desarrollo backend con Spring Boot.
