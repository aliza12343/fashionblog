# Saree & Co — Backend API

Spring Boot REST API for the Saree & Co fashion e-commerce and blog platform. Handles authentication, fashion posts, orders, contact inquiries, and user management.

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.2.4 |
| Security | Spring Security + JWT (jjwt 0.12.3) |
| Database | MySQL 8 / H2 (tests) |
| ORM | Spring Data JPA (Hibernate) |
| Validation | Jakarta Bean Validation |
| API Docs | Swagger / OpenAPI 3 (SpringDoc) |
| Build | Maven 3.9 |
| Testing | JUnit 5, Mockito, JaCoCo |
| Code Quality | SonarCloud (96.4% coverage) |
| CI/CD | GitHub Actions |

---

## Prerequisites

- Java 17+
- MySQL 8+
- Maven 3.9+

---

## Local Setup

### 1 — Create the database

```bash
mysql -u root -p < src/main/resources/db/schema.sql
mysql -u root -p fashion_db < src/main/resources/db/data.sql
```

### 2 — Configure environment variables

Copy the example below and set your own values in `application.properties` or as environment variables:

| Variable | Description | Example |
|---|---|---|
| `DB_URL` | JDBC connection URL | `jdbc:mysql://localhost:3306/fashion_db` |
| `DB_USERNAME` | Database username | `root` |
| `DB_PASSWORD` | Database password | `yourpassword` |
| `JWT_SECRET` | HS256 signing key (min 32 chars) | `my-secret-key-256-bits-minimum!!` |

`src/main/resources/application.properties`:
```properties
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
jwt.secret=${JWT_SECRET}
```

### 3 — Run

```bash
mvn spring-boot:run
```

- API base URL: `http://localhost:8081/api`
- Swagger UI: `http://localhost:8081/swagger-ui.html`

---

## Running Tests

```bash
# Run all tests with coverage report
mvn verify

# View coverage report
open target/site/jacoco/index.html
```

**Test coverage: 96.4%** — measured by JaCoCo and reported to SonarCloud.

---

## API Endpoints

### Authentication

| Method | Path | Auth | Description |
|---|---|---|---|
| POST | `/api/auth/register` | No | Register a new USER account |
| POST | `/api/auth/login` | No | Login — returns JWT token |

**Register request:**
```json
{ "username": "aliza", "password": "password123", "email": "aliza@example.com" }
```

**Login response:**
```json
{ "token": "eyJ...", "username": "aliza", "role": "ROLE_USER" }
```

Pass the token in every protected request:
```
Authorization: Bearer <token>
```

### Posts

| Method | Path | Auth | Role | Description |
|---|---|---|---|---|
| GET | `/api/posts` | Yes | USER / ADMIN | Get 20 most recent posts |
| POST | `/api/posts/add` | Yes | USER / ADMIN | Create a new fashion post |

### Admin

| Method | Path | Auth | Role | Description |
|---|---|---|---|---|
| DELETE | `/api/admin/posts/{id}` | Yes | ADMIN | Delete any post |
| GET | `/api/users` | Yes | ADMIN | List all users |
| GET | `/api/users/{id}` | Yes | ADMIN | Get user by ID |
| DELETE | `/api/users/{id}` | Yes | ADMIN | Delete a user |

### Orders

| Method | Path | Auth | Role | Description |
|---|---|---|---|---|
| POST | `/api/orders` | No | — | Place an order (guest or logged-in) |
| GET | `/api/orders` | Yes | ADMIN | List all orders |
| GET | `/api/orders/{id}` | Yes | ADMIN | Get order by ID |

### Contact

| Method | Path | Auth | Description |
|---|---|---|---|
| POST | `/api/contact` | No | Submit a consultation inquiry |

---

## Seed Credentials

All seed accounts use password `password123`.

| Username | Role |
|---|---|
| `admin` | ADMIN |
| `jane_doe` | USER |
| `fashion_fan` | USER |

---

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   ├── exception/
│   │   │   ├── GlobalExceptionHandler.java   # @ControllerAdvice — consistent error responses
│   │   │   └── ResourceNotFoundException.java
│   │   └── org/example/capstone2/
│   │       ├── Config/
│   │       │   ├── SecurityConfig.java        # JWT filter chain, RBAC rules
│   │       │   ├── WebConfig.java             # CORS configuration
│   │       │   └── RateLimitFilter.java       # Rate limiting on auth endpoints
│   │       ├── controller/
│   │       │   ├── AuthController.java
│   │       │   ├── PostController.java
│   │       │   ├── AdminController.java
│   │       │   ├── OrderController.java
│   │       │   ├── UserController.java
│   │       │   └── ContactController.java
│   │       ├── dto/                           # Request/response data transfer objects
│   │       ├── entity/                        # JPA entities: User, Post, Order
│   │       ├── jwt/
│   │       │   ├── JwtUtil.java               # Token generation and validation
│   │       │   └── JwtAuthFilter.java         # Extracts JWT from Authorization header
│   │       ├── repository/                    # Spring Data JPA interfaces
│   │       └── service/                       # Business logic layer
│   └── resources/
│       ├── db/
│       │   ├── schema.sql                     # DDL with constraints and indexes
│       │   └── data.sql                       # Seed data
│       └── application.properties
└── test/
    └── java/org/example/capstone2/            # JUnit 5 + Mockito test suite
```

---

## CI/CD

GitHub Actions runs on every push to `main`:

1. **Build & Test** — `mvn verify` compiles, runs all tests, generates JaCoCo coverage report
2. **SonarCloud Analysis** — uploads coverage and performs static analysis
3. **Upload Artifacts** — saves Surefire test reports

Secrets required in GitHub repository settings:
- `SONAR_TOKEN` — SonarCloud authentication token

---

## Architecture Decision Records

**ADR-1: JWT over session-based auth**
Chose stateless JWT authentication to support a decoupled React frontend and future mobile clients. Sessions would require sticky sessions or shared session storage.

**ADR-2: H2 for CI, MySQL for production**
Tests run against H2 in-memory database to eliminate external dependencies in CI. Schema is compatible with both. MySQL is used in production for full constraint and index support.

**ADR-3: UUID filenames for uploads**
`FileStorageService` generates UUID-only filenames instead of using the original filename to prevent path traversal attacks (`../` injection via `getOriginalFilename()`).

**ADR-4: Role-based access with two roles**
USER and ADMIN roles kept simple. USERs can create posts and place orders. ADMINs can delete posts, manage users, and view all orders. Enforced at the security filter chain level.
