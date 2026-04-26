# Fashion Blog

A fashion blog and boutique platform where users can discover and share fashion items with pricing and inventory details. Built with Spring Boot 3, Spring Security (JWT), and MySQL.

## Tech Stack

| Layer | Technology |
|---|---|
| Backend | Java 17, Spring Boot 3.2.4 |
| Security | Spring Security + JWT (jjwt 0.12.3) |
| Database | MySQL 8 + Spring Data JPA (Hibernate) |
| API Docs | Swagger / OpenAPI 3 |
| Build | Maven 3.9 |

## Quick Start

### Prerequisites
- Java 17+
- MySQL 8+
- Maven 3.9+

### 1 — Initialize the database
```bash
mysql -u root -p < src/main/resources/db/schema.sql
mysql -u root -p fashion_db < src/main/resources/db/data.sql
```

### 2 — Configure credentials
Edit `src/main/resources/application.properties` and set your MySQL username/password.

### 3 — Run
```bash
mvn spring-boot:run
```

The server starts on **http://localhost:8081**.  
Swagger UI: **http://localhost:8081/swagger-ui.html**

## API Endpoints

| Method | Path | Auth Required | Role | Description |
|--------|------|---------------|------|-------------|
| POST | `/api/auth/register` | No | — | Register a new user |
| POST | `/api/auth/login` | No | — | Login and receive JWT |
| GET | `/api/posts` | Yes | USER / ADMIN | Get all posts (latest 20) |
| POST | `/api/posts/add` | Yes | USER / ADMIN | Create a new post |
| DELETE | `/api/admin/posts/{id}` | Yes | ADMIN | Delete a post |
| POST | `/api/users/register` | No | — | Alternate registration (supports role) |

### Authentication
Pass the JWT returned by `/api/auth/login` in the `Authorization` header:
```
Authorization: Bearer <token>
```

## Seed Credentials
All seed accounts use the password `password123`.

| Username | Role |
|---|---|
| `admin` | ADMIN |
| `jane_doe` | USER |
| `fashion_fan` | USER |

## Project Structure
```
src/main/java/org/example/capstone2/
├── Config/          # SecurityConfig, WebConfig
├── controller/      # AuthController, PostController, AdminController, UserController
├── dto/             # RegisterDTO, LoginDTO, PostDTO, UserDTO
├── entity/          # User, Post, UserRole
├── jwt/             # JwtUtil, JwtAuthFilter
├── repository/      # UserRepository, PostRepository
└── service/         # UserService, PostService, CustomUserDetailsService, FileStorageService

src/main/java/exception/
├── GlobalExceptionHandler.java
└── ResourceNotFoundException.java

src/main/resources/
├── db/
│   ├── schema.sql   # DDL with constraints and indexes
│   └── data.sql     # Seed data
└── application.properties
```
