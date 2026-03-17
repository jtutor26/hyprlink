# HyperLink (Spring Unit Project)

HyperLink is a Spring Boot + Thymeleaf web app for creating a personal "link in bio" profile page.
Users can register, sign in, edit their profile, and publish shareable social links.

## Live deployment

- Web app: deployed on Render
- Database: hosted on Neon (PostgreSQL)
- URL: add your Render service URL here (for example, `hyperlink-tvdj.onrender.com/`)

## What it does

- User registration and login with Spring Security
- Password hashing with BCrypt
- Profile dashboard for editing:
  - display name
  - age/pronouns/bio
  - profile picture URL
  - theme selection
  - social links
- Public profile page by user ID (`/profile/{id}`)
- Seeded starter users loaded on first run

## Tech stack

- Java 25 (from `pom.xml`)
- Spring Boot 4.0.3
- Spring MVC + Thymeleaf
- Spring Data JPA
- Spring Security
- PostgreSQL (Neon in production)
- Docker (for deployment image)
- Maven Wrapper (`./mvnw`)

## Project structure

```text
src/main/java/com/basecamp/HyprLink
  config/
    DataLoader.java
  controller/
    AuthController.java
    DashboardController.java
    ProfileController.java
  entity/
    User.java
    SocialLink.java
  repository/
    UserRepository.java
  security/
    SecurityConfig.java
    CustomUserDetailService.java

src/main/resources
  templates/
    index.html
    dashboard.html
    profile.html
    auth/login.html
    auth/register.html
  static/css/
    landing.css
    auth.css
    default.css
```

## Prerequisites

- JDK 25 installed
- Maven (or use the included Maven Wrapper)
- A PostgreSQL database for local development (Neon or local Postgres)
- (Optional) Docker

## Configuration

Database config is environment-variable based in `src/main/resources/application.properties`:

```properties
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
spring.jpa.hibernate.ddl-auto=update
```

Set these variables before running the app locally:

```bash
export DB_URL="jdbc:postgresql://localhost:5432/quest-log"
export DB_USERNAME="postgres"
export DB_PASSWORD="your-password"
```

For Neon, use the connection values from your Neon project and set them in Render as service environment variables.

## Run locally

```bash
./mvnw spring-boot:run
```

Then open:

- `http://localhost:8080/login`
- `http://localhost:8080/register`

## Run with Docker

```bash
docker build -t hyperlink-app .
docker run -p 8080:8080 \
  -e DB_URL="jdbc:postgresql://<host>:5432/<db>" \
  -e DB_USERNAME="<username>" \
  -e DB_PASSWORD="<password>" \
  hyperlink-app
```

## Deployment notes (Render + Neon)

- Render runs the containerized Spring Boot app from `Dockerfile`.
- Neon provides the managed PostgreSQL database.
- Required env vars on Render:
  - `DB_URL`
  - `DB_USERNAME`
  - `DB_PASSWORD`
- App listens on container port `8080` (from `Dockerfile`).

## Test

```bash
./mvnw test
```

## Seeded demo users

`DataLoader` creates these users on first startup (when DB is empty):

- `johndoe` / `password123`
- `janesmith` / `password123`

## Main routes

- `GET /login` - login page
- `GET /register` - registration form
- `POST /register` - create account
- `GET /dashboard` - authenticated profile editor
- `POST /dashboard/save` - save profile changes
- `GET /profile/{id}` - public profile page

## Notes

- Current JPA mode is `update` (`spring.jpa.hibernate.ddl-auto=update`).
- `profile.html` loads CSS by theme name (`/css/{theme}.css`). The repository currently includes `default.css`.
