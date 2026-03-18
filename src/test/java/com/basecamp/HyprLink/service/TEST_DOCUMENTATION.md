# Service Layer Test Suite Documentation

## Overview
The service-layer test suite verifies business logic in `AuthService`, `DashboardService`, and `ProfileService` with isolated unit tests using Mockito.

This suite includes **15 test cases** that cover:
- successful service behavior
- repository interaction expectations
- filtering and transformation logic
- edge cases and error paths

## Test Configuration
- **Framework:** JUnit 5 (Jupiter)
- **Testing Library:** AssertJ (Fluent Assertions)
- **Mocking:** Mockito (`@ExtendWith(MockitoExtension.class)`)
- **Style:** Arrange-Act-Assert (AAA)

## Test Organization

### AuthService Tests (5 tests)

#### 1. testRegisterUser_EncodesPasswordAndSaves
- **Purpose:** Verify that registration hashes the password before persistence
- **Scenario:** Register a user with plain text password
- **Assertions:**
  - Saved user has encoded password
  - Username remains unchanged

#### 2. testRegisterUser_ReturnsSavedUser
- **Purpose:** Verify service returns repository save result
- **Scenario:** Register valid user
- **Assertions:**
  - Returned instance matches repository response
  - Encoder and repository are called correctly

#### 3. testPrepareRegistrationFormData_InitializesBlankLink
- **Purpose:** Verify register form model starts with one empty social link row
- **Scenario:** Build registration form data
- **Assertions:**
  - User object exists
  - Social links list exists and contains one blank link

#### 4. testPrepareRegistrationFormData_ReturnsFreshInstancePerCall
- **Purpose:** Verify each form preparation call is independent
- **Scenario:** Call form preparation twice
- **Assertions:**
  - Returned users are different instances
  - Social link lists are different instances
  - Both calls include one blank link row

#### 5. testGetAvailableThemes_ReturnsDefaultTheme
- **Purpose:** Verify available themes list
- **Scenario:** Fetch themes for auth pages
- **Assertions:**
  - Exactly `"default"` is returned

### DashboardService Tests (7 tests)

#### 6. testGetUserForDashboard_UserExists_AppendsBlankLink
- **Purpose:** Verify dashboard always includes an additional empty link slot
- **Scenario:** Existing user is loaded
- **Assertions:**
  - User is returned
  - Social link count increases by one
  - Appended link is blank

#### 7. testGetUserForDashboard_UserNotFound_ReturnsNull
- **Purpose:** Verify graceful missing-user behavior
- **Scenario:** Username does not exist
- **Assertions:**
  - Method returns null

#### 8. testUpdateUserProfile_UpdatesFieldsFiltersLinksAndSaves
- **Purpose:** Verify full profile update and social link filtering
- **Scenario:** Updated payload includes valid + invalid links
- **Assertions:**
  - Core profile fields are updated
  - Blank/null links are filtered out
  - Repository save is called with filtered result

#### 9. testUpdateUserProfile_NullSocialLinks_KeepsExistingLinks
- **Purpose:** Verify null social-links input does not erase existing links
- **Scenario:** Updated payload has `socialLinks = null`
- **Assertions:**
  - Basic fields are updated
  - Existing links remain intact
  - Save is still called

#### 10. testUpdateUserProfile_OnlyBlankLinks_ClearsExistingLinks
- **Purpose:** Verify all-invalid social links clear persisted links
- **Scenario:** Updated payload contains only blank link entries
- **Assertions:**
  - Existing links are cleared
  - Save is called with empty link list

#### 11. testUpdateUserProfile_UserNotFound_ThrowsException
- **Purpose:** Verify explicit failure for missing update target user
- **Scenario:** Username not found in repository
- **Assertions:**
  - RuntimeException is thrown
  - Exception message matches expected text

#### 12. testGetAvailableThemes_ReturnsDefaultTheme
- **Purpose:** Verify dashboard theme options
- **Scenario:** Fetch themes
- **Assertions:**
  - Exactly `"default"` is returned

### ProfileService Tests (3 tests)

#### 13. testGetUserProfileById_UserExists_ReturnsUser
- **Purpose:** Verify profile lookup by valid ID
- **Scenario:** Repository returns a matching user
- **Assertions:**
  - Returned object matches repository object
  - Key fields (id/username) are correct

#### 14. testGetUserProfileById_UserMissing_ReturnsNull
- **Purpose:** Verify profile lookup behavior when ID is missing
- **Scenario:** Repository returns empty
- **Assertions:**
  - Method returns null

#### 15. testGetUserProfileById_NullId_ReturnsNull
- **Purpose:** Verify behavior for null ID input
- **Scenario:** Repository returns empty for null ID
- **Assertions:**
  - Method returns null
  - Repository called with null

## Key Testing Strategies

### 1. Service Isolation
- Repositories and encoders are mocked
- Tests validate service logic, not database behavior

### 2. Interaction + State Validation
- Verify both **what changed** (entity fields/lists) and **what was called** (`save`, `findById`, `findByUsername`)

### 3. Input Matrix Coverage
- Valid input
- Missing user input
- Null collections
- Blank/invalid social links
- Null ID path

### 4. Consistent Style
- `@DisplayName` used across all tests
- Method names follow `testMethod_Scenario_Expected`
- Sections grouped with comment banners to match repository test readability

## Running the Tests

### Run all service tests
```bash
./mvnw test -Dtest=AuthServiceTest,DashboardServiceTest,ProfileServiceTest
```

### Run one class
```bash
./mvnw test -Dtest=DashboardServiceTest
```

### Run one test method
```bash
./mvnw test -Dtest=DashboardServiceTest#testUpdateUserProfile_UpdatesFieldsFiltersLinksAndSaves
```

## Test Results

✅ Service test suite passes successfully
- **Tests run:** 15
- **Failures:** 0
- **Errors:** 0
- **Skipped:** 0

## Coverage Summary

The suite covers:
- ✅ Auth registration password encoding workflow
- ✅ Registration form model initialization logic
- ✅ Dashboard model preparation logic
- ✅ Profile update mapping behavior
- ✅ Social link filtering rules
- ✅ Missing-user exception path
- ✅ Profile retrieval success and null-return paths

## Future Enhancements
- Add negative tests for malformed profile field input (if validation rules are introduced)
- Add parameterized tests for social link filtering combinations
- Add tests for additional theme options if themes become dynamic

