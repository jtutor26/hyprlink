# HyperLink (Spring Unit Project)

HyperLink is a Spring Boot + Thymeleaf web app for creating a personal "link in bio" profile page.
Users can register, sign in, edit their profile, and publish shareable social links.

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
- PostgreSQL (runtime)
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
- PostgreSQL running locally
- A database named `quest-log`
- (Optional) IntelliJ IDEA

## Configuration

Current DB settings are in `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/quest-log
spring.datasource.username=postgres
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=create
```

Update `username`/`password` as needed for your local PostgreSQL setup.

## Run locally

```bash
./mvnw spring-boot:run
```

Then open your browser and start with:

- `http://localhost:8080/login`
- `http://localhost:8080/register`

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

- `spring.jpa.hibernate.ddl-auto=create` recreates schema on startup; switch to a safer value for persistent data.
- `profile.html` loads CSS by theme name (`/css/{theme}.css`). The repository currently includes `default.css`.

