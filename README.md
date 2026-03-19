# HyperLink (Spring Unit Project)

HyperLink is a Spring Boot + Thymeleaf web app for creating a personal "link in bio" profile page.
Users can register, sign in, edit their profile, and share a public profile page.

## Current project status

- Core authentication flow is implemented (`/login`, `/register`, secured routes).
- Dashboard profile editing is implemented, including social links and style options.
- File uploads are supported for profile and background images from the dashboard.
- Public profile rendering is implemented at `/profile/{id}`.
- Seed data is loaded at startup when the user table is empty.
- Automated tests are in place and currently passing (`48/48` in latest local run).

## What it does

- User registration and login with Spring Security.
- Password hashing with BCrypt.
- Dashboard editing for:
  - display name
  - age / pronouns / bio
  - profile image URL or uploaded profile image
  - uploaded/custom background image
  - theme + link/button/text style options
  - social links
- Public profile page by user ID (`/profile/{id}`).

## Tech stack

- Java 25
- Spring Boot 4.0.3
- Spring MVC + Thymeleaf
- Spring Data JPA (Hibernate)
- Spring Security
- PostgreSQL (runtime configuration)
- H2 in-memory database for tests
- Docker (multi-stage image)
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
  service/
    AuthService.java
    DashboardService.java
    ProfileService.java

src/main/resources
  templates/
    index.html
    dashboard.html
    profile.html
    auth/login.html
    auth/register.html
  static/css/
    auth.css
    dashboard.css
    default.css
    landing.css
    login.css
    register.css
```

## Prerequisites

- JDK 25
- Maven (or use the included Maven Wrapper)
- PostgreSQL database for local app runtime
- (Optional) Docker

## Configuration

Main config: `src/main/resources/application.properties`

```properties
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
spring.datasource.driver-class-name=org.postgresql.Driver
spring.jpa.hibernate.ddl-auto=create
server.port=${PORT:8080}
```

Set local runtime variables:

```bash
export DB_URL="jdbc:postgresql://localhost:5432/your_database"
export DB_USERNAME="postgres"
export DB_PASSWORD="your_password"
```

## Run locally

```bash
./mvnw spring-boot:run
```

Then open:

- `http://localhost:8080/`
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

## Main routes

- `GET /` - landing/welcome page (template `index.html`)
- `GET /login` - login form
- `GET /register` - registration form
- `POST /register` - account creation
- `GET /dashboard` - authenticated profile editor
- `POST /dashboard/save` - save dashboard profile changes
- `GET /profile/{id}` - public profile page
- `GET /images/background-templates/{filename}` - serve background template images

## Seeded demo users

`DataLoader` creates these users on first startup when the database is empty:

- `johndoe` / `password123`
- `janesmith` / `password123`

## Testing

Run all tests:

```bash
./mvnw test
```

### Latest verified test result

- Date: `2026-03-18`
- Command: `./mvnw test`
- Result: `BUILD SUCCESS`
- Totals: `48 tests, 0 failures, 0 errors, 0 skipped`

### Test suites and cases

| Test class | Layer | Cases |
| --- | --- | ---: |
| `UserRepositoryTest` | Repository integration (`@SpringBootTest`, H2) | 17 |
| `SecurityConfigTest` | Security config unit | 4 |
| `CustomUserDetailServiceTest` | Security service unit | 2 |
| `ProfileControllerTest` | Controller unit | 3 |
| `DashboardControllerTest` | Controller unit | 3 |
| `AuthControllerTest` | Controller unit | 3 |
| `DashboardServiceTest` | Service unit | 7 |
| `AuthServiceTest` | Service unit | 5 |
| `ProfileServiceTest` | Service unit | 3 |
| `SpringUnitProjectApplicationTests` | Context load smoke test | 1 |

### Notes on test configuration

- Test properties are in `src/test/resources/application.properties`.
- Tests use H2 (`jdbc:h2:mem:testdb`) instead of PostgreSQL.
- Repository tests run with Spring context and transactions.
- Most controller/service/security tests use JUnit 5 + Mockito.

## Notes

- Security allows public access to `/`, `/login`, `/register`, `/profile/**`, and `/css/**`.
- Other routes require authentication.
- The app currently sets `spring.jpa.hibernate.ddl-auto=create` in main runtime config.
