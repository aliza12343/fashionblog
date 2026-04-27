# Architecture Document
## Fashion Blog & Boutique Platform

---

## 1. System Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────┐
│                        CLIENT LAYER                             │
│          (React SPA / Postman / Swagger UI / Mobile)            │
└───────────────────────────┬─────────────────────────────────────┘
                            │  HTTPS  (JSON + JWT Bearer Token)
┌───────────────────────────▼─────────────────────────────────────┐
│                     SPRING BOOT 3 API SERVER                    │
│                        (localhost:8081)                         │
│                                                                 │
│  ┌──────────────┐   ┌────────────────────────────────────────┐  │
│  │  JWT Filter  │──▶│           Controller Layer             │  │
│  │(JwtAuthFilter│   │  AuthController  /api/auth/**          │  │
│  │  validates   │   │  PostController  /api/posts/**         │  │
│  │  Bearer tok) │   │  AdminController /api/admin/**         │  │
│  └──────────────┘   │  UserController  /api/users/**         │  │
│                     └────────────────┬───────────────────────┘  │
│  ┌──────────────┐                    │                           │
│  │ Spring Sec.  │   ┌────────────────▼───────────────────────┐  │
│  │ (RBAC Rules) │   │           Service Layer                │  │
│  │  USER/ADMIN  │   │  UserService   PostService             │  │
│  └──────────────┘   │  CustomUserDetailsService              │  │
│                     │  FileStorageService                    │  │
│  ┌──────────────┐   └────────────────┬───────────────────────┘  │
│  │Global Except.│                    │                           │
│  │  Handler     │   ┌────────────────▼───────────────────────┐  │
│  │(@RestCtrlAdv)│   │         Repository Layer               │  │
│  └──────────────┘   │  UserRepository   PostRepository       │  │
│                     │  (Spring Data JPA + custom @Query)     │  │
│                     └────────────────┬───────────────────────┘  │
│                                      │  JPA / Hibernate          │
└──────────────────────────────────────┼──────────────────────────┘
                                       │
                    ┌──────────────────▼──────────────┐
                    │        MySQL 8 Database         │
                    │          fashion_db             │
                    │   tables: users, posts          │
                    └─────────────────────────────────┘
                    
                    ┌─────────────────────────────────┐
                    │       File System (./uploads)   │
                    │  UUID-prefixed image files      │
                    └─────────────────────────────────┘
```

**Request flow:**
1. Client sends request with `Authorization: Bearer <jwt>` header.
2. `JwtAuthFilter` validates the token and sets the `SecurityContext`.
3. Spring Security checks role requirements for the target endpoint.
4. Controller delegates business logic to the Service layer.
5. Service calls Repository which issues JPA/SQL queries to MySQL.
6. Response is serialised to JSON and returned with the appropriate HTTP status.

---

## 2. Entity-Relationship Diagram (ERD)

```
┌─────────────────────────────────────┐
│               users                 │
├─────────────────────────────────────┤
│ PK  id            BIGINT  NOT NULL  │
│     username      VARCHAR(50)   NN  │◄── UNIQUE
│     password      VARCHAR(255)  NN  │
│     email         VARCHAR(100)  NN  │◄── UNIQUE
│     role          ENUM('USER',      │
│                   'ADMIN')      NN  │
└──────────┬───────────────┬──────────┘
           │ 1             │ 1
           │               │
     has many         has many (optional)
           │               │
           │ N             │ N
┌──────────▼──────────┐  ┌─▼───────────────────────────────────┐
│        posts        │  │               orders                │
├─────────────────────┤  ├─────────────────────────────────────┤
│ PK id   BIGINT  NN  │  │ PK  id             BIGINT  NOT NULL │
│    title  V(200) NN │  │     customer_name  VARCHAR(200)  NN │
│    content TEXT  NN │  │     email          VARCHAR(100)  NN │
│    image_url V(500) │  │     phone          VARCHAR(30)      │
│    price  DOUBLE    │  │     shipping_addr  VARCHAR(255)  NN │
│    category V(100)  │  │     city           VARCHAR(100)  NN │
│    stock_qty INT    │  │     state          VARCHAR(50)      │
│    created_at DT NN │  │     zip            VARCHAR(20)   NN │
│ FK user_id  BIGINT  │  │     country        VARCHAR(100)  NN │
│    ──▶ users.id     │  │     total          DOUBLE           │
│    ON DELETE CASCADE│  │     status         VARCHAR(20)   NN │
└─────────────────────┘  │     created_at     DATETIME      NN │
                         │ FK  user_id        BIGINT  NULL     │
                         │     ──▶ users.id (nullable —        │
                         │         guest checkout allowed)     │
                         └─────────────────────────────────────┘

Indexes:
  idx_posts_category    posts(category)
  idx_posts_created_at  posts(created_at DESC)
  idx_posts_user_id     posts(user_id)
  idx_users_email       users(email)
```

**Relationships:**
- `users` → `posts`: One-to-Many (one user owns many posts). Cascade delete.
- `users` → `orders`: One-to-Many, nullable FK (guest checkout sets user_id = NULL).
- Deleting a user cascades to delete all their posts (`ON DELETE CASCADE`).

---

## 3. API Design Document

Base URL: `http://localhost:8081`  
All request/response bodies use `Content-Type: application/json`.

---

### 3.1 Authentication Endpoints

#### POST `/api/auth/register`
Register a new user account.

**Request Body:**
```json
{
  "username": "jane_doe",
  "password": "secret123",
  "email": "jane@example.com"
}
```
**Validation:** `username` 3–50 chars, `password` min 6 chars, `email` valid format.

**Responses:**

| Status | Description | Body |
|--------|-------------|------|
| 201 | Created | `{ "message": "User registered successfully", "username": "jane_doe" }` |
| 400 | Username taken / validation error | `{ "timestamp": "...", "status": 400, "error": "..." }` |

---

#### POST `/api/auth/login`
Authenticate and receive a JWT.

**Request Body:**
```json
{
  "username": "jane_doe",
  "password": "secret123"
}
```

**Responses:**

| Status | Description | Body |
|--------|-------------|------|
| 200 | OK | `{ "token": "<jwt>", "username": "jane_doe", "role": "ROLE_USER" }` |
| 401 | Bad credentials | Spring Security default error |

---

### 3.2 Post Endpoints

All endpoints require `Authorization: Bearer <jwt>` header (USER or ADMIN role).

#### GET `/api/posts`
Returns the 20 most recent posts.

**Responses:**

| Status | Description | Body |
|--------|-------------|------|
| 200 | OK | Array of Post objects |
| 401 | Missing / invalid token | — |

**Post object schema:**
```json
{
  "id": 1,
  "title": "Summer Floral Dress",
  "content": "Light and breezy...",
  "imageUrl": null,
  "price": 49.99,
  "category": "Dresses",
  "stockQuantity": 25,
  "createdAt": "2025-06-01T10:30:00",
  "user": { "id": 2, "username": "jane_doe", "email": "jane@example.com", "role": "USER" }
}
```

---

#### POST `/api/posts/add`
Create a new post.

**Request Body:** Post object (same schema, omit `id` and `createdAt`).

**Responses:**

| Status | Description | Body |
|--------|-------------|------|
| 201 | Created | Saved Post object |
| 400 | Validation error | `{ "field": "error message", ... }` |
| 401 | Unauthenticated | — |

---

### 3.3 Admin Endpoints

Requires ADMIN role.

#### DELETE `/api/admin/posts/{id}`

| Parameter | Type | Description |
|---|---|---|
| `id` (path) | Long | ID of the post to delete |

**Responses:**

| Status | Description | Body |
|--------|-------------|------|
| 200 | Deleted | `"Post successfully deleted by Admin."` |
| 404 | Post not found | `{ "timestamp": "...", "status": 404, "error": "Post not found with id: ..." }` |
| 403 | Not an admin | — |

---

### 3.4 User Endpoints

#### POST `/api/users/register`
Alternative registration supporting explicit role assignment.

**Request Body:**
```json
{
  "username": "new_admin",
  "password": "secret123",
  "email": "newadmin@example.com",
  "role": "ADMIN"
}
```

**Responses:**

| Status | Description | Body |
|--------|-------------|------|
| 200 | OK | `"User registered successfully!"` |
| 400 | Username exists / validation error | `"Username already exists!"` |

---

### 3.5 Standard Error Envelope

All handled errors return this structure:
```json
{
  "timestamp": "2025-06-01T10:30:00.123",
  "status": 400,
  "error": "Descriptive error message"
}
```
Validation errors (400) return a map of field names to messages:
```json
{
  "username": "Username is required",
  "email": "Must be a valid email address"
}
```

---

## 4. React Frontend Component Diagram

> Note: The React frontend is maintained in a separate repository (`Fashion-frontend`).  
> This diagram describes the intended component structure.

```
App
├── Router
│   ├── PublicRoute  (/login, /register)
│   └── PrivateRoute (requires JWT in localStorage)
│
├── pages/
│   ├── LoginPage        → calls POST /api/auth/login
│   ├── RegisterPage     → calls POST /api/auth/register
│   ├── PostFeedPage     → calls GET  /api/posts
│   ├── CreatePostPage   → calls POST /api/posts/add
│   └── AdminDashboard   → calls DELETE /api/admin/posts/:id
│
├── components/
│   ├── Navbar           (auth state, role-based links)
│   ├── PostCard         (displays single post)
│   ├── PostForm         (create/edit post form with validation)
│   └── ProtectedRoute   (redirect to /login if no token)
│
└── services/
    ├── authService.js   (login, register, token storage)
    └── postService.js   (CRUD wrappers with axios + auth header)
```

**State management:** React Context (AuthContext) holds the current user and JWT.  
**HTTP client:** Axios with a request interceptor to inject `Authorization: Bearer` on every call.
