# stddisciplineproj
Group Project (Student Discipline System)
# Student Discipline System

A Spring Boot web application for managing student discipline records, incidents, sanctions, feedback, and anonymous reports. Role-based access for **Admin**, **Teacher**, and **Student** users.

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Backend | Spring Boot 3.2, Spring Security, Spring Data JPA |
| Frontend | Thymeleaf, HTML/CSS/JS |
| Database | MySQL (hosted on Clever Cloud) |
| Build | Gradle |

---

## Project Structure

```
src/main/
├── java/com/studentdiscipline/
│   ├── config/          ← Spring Security
│   ├── controller/      ← Web endpoints
│   ├── model/           ← JPA entities
│   ├── repository/      ← DB access
│   ├── service/         ← Business logic
│   └── enums/           ← Role, IncidentType, etc.
└── resources/
    ├── templates/       ← Thymeleaf HTML pages
    │   ├── auth/
    │   ├── admin/
    │   ├── teacher/
    │   └── student/
    └── static/          ← CSS, JS
```

---

## Roles & Access

| Role | Access |
|------|--------|
| ADMIN | Full access — manage teachers, students, all incidents, sanctions, feedback, reports, analytics |
| TEACHER | Manage own students' incidents and sanctions, submit feedback, view own activity |
| STUDENT | View own record and sanctions, submit feedback and anonymous reports |

---

## Running Locally

### Prerequisites
- Java 17+
- MySQL running locally (or Clever Cloud credentials)
- Gradle

### Steps

1. **Clone the repository**
   ```bash
   git clone <repo-url>
   cd student-discipline-system
   ```

2. **Configure the database**

   Open `src/main/resources/application.properties` and either:
   - Set your Clever Cloud environment variables, or
   - Uncomment the local dev block and fill in your local MySQL credentials

3. **Run the app**
   ```bash
   ./gradlew bootRun
   ```

4. **Open in browser**
   ```
   http://localhost:8080
   ```

---

## Clever Cloud Deployment

1. Create a **Java + Gradle** application on [Clever Cloud](https://www.clever-cloud.com/).
2. Add a **MySQL add-on** and link it to your app.
3. Clever Cloud automatically injects these environment variables:
   - `MYSQL_ADDON_HOST`
   - `MYSQL_ADDON_PORT`
   - `MYSQL_ADDON_DB`
   - `MYSQL_ADDON_USER`
   - `MYSQL_ADDON_PASSWORD`
4. Push your code:
   ```bash
   git remote add clever <clever-cloud-git-url>
   git push clever main
   ```
5. The app will build and deploy automatically.

> **Note:** `spring.jpa.hibernate.ddl-auto=update` will auto-create all tables on first run.

---

## Team Members

| Member | Responsibility |
|--------|---------------|
| Member 1 | SecurityConfig, AuthController, User model, login page |
| Member 2 | StudentController, Student/Teacher models, StudentService |
| Member 3 | IncidentController, SanctionController, AnonymousReportController |
| Member 4 | All `templates/admin/` pages |
| Member 5 | All `templates/teacher/` and `templates/student/` pages |
| Member 6 | Feedback & ActivityLog (model/repo/service), Role enum, application.properties, build.gradle, deployment |

---

## Key Notes for Integration

- **ActivityLogService** should be injected and called by **all other services** after any create/update/delete action to maintain a full audit trail.
- **FeedbackService** depends on **ActivityLogService** — ensure both beans are present.
- The `Role` enum (`ADMIN`, `TEACHER`, `STUDENT`) is used in `User.java` (Member 1) and referenced across all services.
