# Controller Layer Test Suite Documentation

## Overview
The controller-layer test suite verifies request-handling behavior for `AuthController`, `DashboardController`, and `ProfileController`.

This suite includes **9 unit tests** that validate:
- returned view names
- model attribute population
- redirects for form submissions
- repository/service delegation
- edge-case controller flows

## Test Configuration
- **Framework:** JUnit 5 (Jupiter)
- **Testing Library:** AssertJ (Fluent Assertions)
- **Mocking:** Mockito (`@ExtendWith(MockitoExtension.class)`)
- **Model Type:** `ExtendedModelMap` for controller model assertions
- **Style:** Arrange-Act-Assert (AAA)

## Test Organization

### AuthController Tests (3 tests)

#### 1. testShowLoginForm_ReturnsLoginView
- **Purpose:** Verify login endpoint returns correct view
- **Scenario:** Invoke `showLoginForm()`
- **Assertions:**
  - Returned view is `auth/login`

#### 2. testShowRegistrationForm_PopulatesModelAndReturnsRegisterView
- **Purpose:** Verify register page model setup
- **Scenario:** Invoke `showRegistrationForm(model)` with mocked service data
- **Assertions:**
  - Returned view is `auth/register`
  - Model contains `user` and `themes`
  - Auth service methods are called

#### 3. testRegisterUser_CallsServiceAndRedirects
- **Purpose:** Verify register submit flow
- **Scenario:** Invoke `registerUser(user)`
- **Assertions:**
  - Auth service is called with incoming user data
  - Redirect is `redirect:/login?success`

### DashboardController Tests (3 tests)

#### 4. testShowDashboard_UserExists_PopulatesModelAndReturnsDashboardView
- **Purpose:** Verify dashboard page model setup for authenticated user
- **Scenario:** Invoke `showDashboard(principal, model)` with existing user from repository
- **Assertions:**
  - Returned view is `dashboard`
  - Model contains `user` and dashboard customization collections
  - Controller appends one blank social-link row for dashboard editing

#### 5. testSaveProfile_ValidUpdates_FiltersLinksAndRedirects
- **Purpose:** Verify dashboard save submission behavior
- **Scenario:** Invoke `saveProfile(updatedData, principal, null, null)`
- **Assertions:**
  - User fields are updated from posted model data
  - Invalid blank links are filtered out before save
  - Redirect is `redirect:/dashboard?success`

#### 6. testHandleUploadTooLarge_RedirectsWithFlag
- **Purpose:** Verify upload exception redirect behavior
- **Scenario:** Invoke `handleUploadTooLarge()`
- **Assertions:**
  - Redirect is `redirect:/dashboard?uploadTooLarge`

### ProfileController Tests (3 tests)

#### 7. testGetProfile_UserExists_ReturnsProfileView
- **Purpose:** Verify public profile page for existing user
- **Scenario:** Invoke `getProfile(id, model)` with found user
- **Assertions:**
  - Returned view is `profile`
  - Model contains `user`
  - Profile service is called with expected id

#### 8. testGetProfile_UserMissing_Returns404View
- **Purpose:** Verify 404 flow for missing profile
- **Scenario:** Invoke `getProfile(id, model)` with null service result
- **Assertions:**
  - Returned view is `error/404`
  - Model does not contain `user`

#### 9. testGetProfile_NullId_Returns404View
- **Purpose:** Verify null-id edge case behavior
- **Scenario:** Invoke `getProfile(null, model)`
- **Assertions:**
  - Returned view is `error/404`
  - Model does not contain `user`
  - Service lookup is still invoked

## Key Testing Strategies

### 1. Controller Isolation
- Dependencies are mocked to isolate controller responsibilities
- Tests focus on MVC behavior (view/model/redirect) and delegation

### 2. View + Model Validation
- Each GET endpoint validates both returned view and model content
- Missing-data paths verify fallback behavior (null model user or 404 view)

### 3. Delegation Verification
- POST flows verify repository/service calls with expected inputs
- Principal-based methods verify username propagation

### 4. Consistent Test Style
- `@DisplayName` is used for all tests
- Method names follow `testMethod_Scenario_Expected`
- Tests use clear AAA comments and section banners

## Running the Tests

### Run all controller tests:
```bash
./mvnw test -Dtest=AuthControllerTest,DashboardControllerTest,ProfileControllerTest
```

### Run one controller test class:
```bash
./mvnw test -Dtest=DashboardControllerTest
```

### Run a specific test method:
```bash
./mvnw test -Dtest=DashboardControllerTest#testHandleUploadTooLarge_RedirectsWithFlag
```

## Test Results

✅ All 9 controller tests pass successfully
- **Failures:** 0
- **Errors:** 0
- **Skipped:** 0

## Coverage Summary

The suite covers:
- ✅ Auth page routing and registration redirect flow
- ✅ Dashboard view-model composition, save filtering, and upload exception redirect handling
- ✅ Profile page success and 404 branches
- ✅ Principal username propagation for dashboard endpoints
- ✅ Null/missing edge cases in controller logic

## Future Enhancements
- Add MockMvc tests for endpoint-level HTTP assertions
- Add tests for multipart upload success paths
- Add tests for validation/error feedback once controller validation is introduced
