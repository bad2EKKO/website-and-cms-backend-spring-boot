# Website & CMS Backend (Spring Boot)

A REST API backend for a department website (ITMD) and its content management system.
The public website reads published content from the API, and administrators manage that content
(news, events, reports, documents) through secured admin endpoints.

![Java](https://img.shields.io/badge/Java-17-orange?logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.1-6DB33F?logo=springboot&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring%20Security-JWT-6DB33F?logo=springsecurity&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-8-4479A1?logo=mysql&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-build-C71A36?logo=apachemaven)

---

## Features

- **JWT authentication.** Stateless sign-in and sign-up with BCrypt password hashing.
- **Role-based access control.** Four roles: `SUPER_ADMIN`, `ADMIN`, `MODERATOR` and `USER`. Only a super admin can delete content.
- **News management.** Create, edit, publish or unpublish, and delete news. The public site gets the latest 5 active items.
- **Action Plan & Performance Report PDFs.** Upload PDFs, and the server makes a PNG preview of the first page with Apache PDFBox.
- **Events with media galleries.** Create events and upload multiple images or videos to each one.
- **Hardware specifications.** Group specification documents by type, and upload and download them.
- **Contact / inquiries form.** Sends the inquiry to the department by email and an automatic reply to the sender (Spring Mail over SMTP).
- **Status toggling.** Every content type has an active/inactive status, so content can be hidden without deleting it.
- **Default data seeding.** Roles and the initial admin accounts are created on first startup.

## Tech Stack

| Layer          | Technology                                  |
| -------------- | ------------------------------------------- |
| Language       | Java 17                                     |
| Framework      | Spring Boot 3.3 (Web, Data JPA, Validation) |
| Security       | Spring Security, JWT (jjwt 0.11.5)          |
| Database       | MySQL (Hibernate ORM)                       |
| PDF processing | Apache PDFBox 3                             |
| Email          | Spring Boot Starter Mail (Gmail SMTP)       |
| Build          | Maven (wrapper included)                    |

## Project Structure

```
src/main/java/com/MOF_ITMD/DepWeb
├── controllers/     # REST controllers: Auth, Public, Admin, Test
├── def/             # DefaultUserInitializer, seeds roles and default users
├── dto/             # Request DTOs for updates and status changes
├── models/          # JPA entities: News, Event, Media, ActionPdf, PerformancePdf,
│   └── users/       #   HardwareSpecification, SpecifyDoc, User, Role ...
├── payload/         # Login/Signup requests, JWT/Message responses
├── repository/      # Spring Data JPA repositories
├── security/        # Security config, JWT filter/utils, UserDetails service
└── service/         # Business logic and file handling
```

## API Overview

### Authentication: `/api/auth`

| Method | Endpoint  | Description                     |
| ------ | --------- | ------------------------------- |
| POST   | `/signin` | Log in and receive a JWT token  |
| POST   | `/signup` | Register a new user             |

### Public: `/api/public` (no token required)

| Method | Endpoint                                         | Description                          |
| ------ | ------------------------------------------------ | ------------------------------------ |
| POST   | `/inquiries`                                     | Submit the contact form              |
| GET    | `/news/latest`                                   | Latest 5 active news items           |
| GET    | `/action-pdfs`                                   | List Action Plan PDFs                |
| GET    | `/action-pdf/pdf/{name}`                         | Download an Action Plan PDF          |
| GET    | `/action-pdf/image/{name}`                       | Action Plan preview image            |
| GET    | `/performance-pdfs`                              | List Performance Report PDFs         |
| GET    | `/performance-pdf/pdf/{name}`                    | Download a Performance Report PDF    |
| GET    | `/performance-pdf/image/{name}`                  | Performance Report preview image     |
| GET    | `/events`                                        | List events                          |
| GET    | `/event/content/{eventId}`                       | Media items for an event             |
| GET    | `/event/content/serve/{contentName}`             | Serve an event image or video        |
| GET    | `/hardware-specifications`                       | List hardware specifications         |
| GET    | `/hardware-specification/{specId}/specify-docs`  | Documents for a specification        |
| GET    | `/hardware-specification/specify-doc/doc/{id}`   | Download a specification document    |

### Admin: `/api/admin` (requires `Authorization: Bearer <token>`)

| Resource                | Endpoints                                                                                     |
| ----------------------- | --------------------------------------------------------------------------------------------- |
| News                    | `POST /news`, `GET /news`, `GET /news/{id}`, `PUT /news/{id}`, `PUT /news/status/{id}`, `DELETE /news/{id}` |
| Action Plan PDFs        | `POST /action-pdf`, `PUT /action-pdf/status/{id}`, `DELETE /action-pdf/{id}`                  |
| Performance Reports     | `POST /performance-pdf`, `PUT /performance-pdf/status/{id}`, `DELETE /performance-pdf/{id}`   |
| Events                  | `POST /event`, `POST /event/{eventId}/media`, `PUT /event/status/{eventId}`, `DELETE /event/{eventId}` |
| Hardware Specifications | `POST /hardware-specification`, `POST /hardware-specification/specify-doc/{specId}`, `PUT .../status/{specId}`, `PUT .../specify-doc/status/{docId}`, `DELETE /hardware-specification/{specId}`, `DELETE .../specify-doc/{docId}` |

> Every `DELETE` request under `/api/admin/**` requires the `SUPER_ADMIN` role.

## Getting Started

### Prerequisites

- Java 17+
- MySQL 8 running locally
- (Optional) A Gmail account with an [App Password](https://support.google.com/accounts/answer/185833) for the inquiry emails

### 1. Clone the repository

```bash
git clone https://github.com/bad2EKKO/website-and-cms-backend-spring-boot.git
cd website-and-cms-backend-spring-boot
```

### 2. Configure the application

Edit `src/main/resources/application.yaml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://127.0.0.1:3306/web_test?createDatabaseIfNotExist=true
    username: root
    password: root

  app:
    jwtSecret: <your-256-bit-hex-secret>
    jwtExpirationMs: 3600000        # 1 hour

  mail:
    username: your-email@gmail.com
    password: your-app-password

  servlet:
    multipart:
      location: /path/to/upload/folder/   # where uploaded files are stored (keep the trailing slash)
```

Also set the inquiry recipient address in `service/InquiriesService.java`.

The database and tables are created automatically (`ddl-auto: update`).

### 3. Run

```bash
./mvnw spring-boot:run
```

The API starts at `http://localhost:8080`.

### Default accounts

On first startup the app creates these accounts:

| Username | Password   | Role          |
| -------- | ---------- | ------------- |
| `super`  | `super123` | `SUPER_ADMIN` |
| `admin`  | `admin123` | `ADMIN`       |

> ⚠️ Change these passwords and the JWT secret before deploying anywhere public.

### Example: log in and call an admin endpoint

```bash
# Sign in
curl -X POST http://localhost:8080/api/auth/signin \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'

# Use the returned token
curl http://localhost:8080/api/admin/news \
  -H "Authorization: Bearer <token>"
```

## Uploaded File Layout

Uploaded files are stored on disk under the configured multipart `location`:

```
<upload-location>/
├── Action Plan Pdf/
│   └── Action Plan Image/          # generated first-page previews
├── Performance Report Pdf/
│   └── Performance Report Image/
├── Events/<event-name>/            # event images and videos
└── Hardware Specification/<type>/  # specification documents
```

The database stores only the file names, paths and metadata.
