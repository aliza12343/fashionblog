# Project Requirements Document
## Fashion Blog & Boutique Platform

---

## 1. Problem Statement & Business Justification

Independent fashion creators and small boutique owners have no simple platform to showcase inventory, share styling content, and reach customers simultaneously. Existing solutions are either too complex (full e-commerce suites) or too limited (pure blogging tools with no inventory awareness).

This platform solves that gap by combining fashion blog content with lightweight inventory tracking — each post carries a price, stock quantity, and category, giving creators a single place to publish looks and manage stock without a separate system.

**Business value:**
- Reduces operational overhead for small boutiques
- Increases discoverability for independent fashion creators
- Provides role-based administration without a paid CMS

---

## 2. Target Users

| User Type | Description |
|---|---|
| **Fashion Creator / Blogger** | Registered user who publishes fashion posts |
| **Shop Visitor / Browser** | Authenticated user who browses published content |
| **Platform Administrator** | Admin-role user who moderates content and manages accounts |

---

## 3. User Stories

| # | As a… | I want to… | So that… |
|---|---|---|---|
| US-01 | Visitor | Register for an account with username, email, and password | I can access the platform |
| US-02 | Registered user | Log in with my credentials and receive a token | I stay authenticated across requests |
| US-03 | Authenticated user | Browse all fashion posts ordered by newest first | I can discover the latest styles |
| US-04 | Authenticated user | Create a post with a title, description, category, price, and stock level | I can share items with the community |
| US-05 | Authenticated user | Upload an image with my post | I can visually showcase items |
| US-06 | Authenticated user | See the stock quantity on a post | I can tell if an item is available |
| US-07 | Admin | Delete any post from the platform | I can remove inappropriate or outdated content |
| US-08 | Admin | Register users with specific roles | I can grant admin access when needed |
| US-09 | Authenticated user | Receive descriptive error messages on bad input | I understand what went wrong without guessing |
| US-10 | Developer / Integrator | Access interactive API documentation | I can explore and test endpoints without reading source code |

---

## 4. Functional Requirements

| ID | Requirement |
|---|---|
| FR-01 | The system shall allow users to register with a unique username (3–50 chars) and unique email address. |
| FR-02 | The system shall reject registration attempts with a duplicate username or email and return a 400 error. |
| FR-03 | The system shall authenticate users via username and password and issue a signed JWT valid for 24 hours. |
| FR-04 | The system shall return the JWT token, username, and role on successful login. |
| FR-05 | The system shall allow authenticated users to create posts containing: title, content, category, price, stock quantity, and optional image URL. |
| FR-06 | The system shall return the 20 most recent posts ordered by creation date descending. |
| FR-07 | The system shall restrict post deletion to users holding the ADMIN role; all other users receive 403. |
| FR-08 | The system shall support two roles: USER (default) and ADMIN. |
| FR-09 | The system shall hash all passwords using BCrypt before persistence. |
| FR-10 | The system shall validate all incoming request bodies and return field-level error messages (400) on validation failure. |
| FR-11 | The system shall support image file uploads up to 10 MB and store files with UUID-prefixed filenames. |
| FR-12 | The system shall expose interactive API documentation at `/swagger-ui.html`. |
| FR-13 | The system shall return a consistent JSON error envelope (`timestamp`, `status`, `error`) for all handled exceptions. |

---

## 5. Non-Functional Requirements

### Performance
- All read endpoints (GET /api/posts) shall respond within 500 ms under normal load.
- Pagination is applied to list queries (default page size: 20) to prevent unbounded result sets.

### Security
- Passwords are never stored or transmitted in plain text; BCrypt is used exclusively.
- All non-public endpoints require a valid JWT in the `Authorization: Bearer` header.
- Spring Security enforces role-based access control at the filter-chain level.
- CSRF protection is disabled (stateless JWT API — no session cookies).

### Scalability
- JWT-based stateless authentication allows horizontal scaling without shared session storage.
- Database indexes are defined on `posts.category`, `posts.created_at`, `posts.user_id`, and `users.email` to support efficient querying as data grows.

### Maintainability
- All API endpoints are annotated with Swagger `@Operation` and `@ApiResponse` for self-documenting contracts.
- A global exception handler (`@RestControllerAdvice`) centralises error formatting and logging.

---

## 6. Out-of-Scope Items

The following features are explicitly excluded from this release:

- Payment processing and checkout flow
- Shopping cart / wishlist functionality
- Email notifications (registration confirmation, order updates)
- Social features: likes, comments, follows, shares
- Full-text search or category filtering via the API
- Frontend / React client (backend API only)
- Automated CI/CD pipeline configuration
