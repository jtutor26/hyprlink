# Security Package Test Suite Documentation

## Overview
The security-layer test suite validates behavior in `CustomUserDetailService` and `SecurityConfig`.

This suite includes **6 tests** that cover:
- user-details lookup and authority mapping
- missing-user exception handling
- password encoder bean behavior
- security filter-chain builder flow
- application of core security configuration blocks

## Test Configuration
- **Framework:** JUnit 5 (Jupiter)
- **Testing Library:** AssertJ (Fluent Assertions)
- **Mocking:** Mockito (`@ExtendWith(MockitoExtension.class)`)
- **Security Config Testing:** Unit tests with mocked `HttpSecurity` and `SecurityFilterChain`
- **Style:** Arrange-Act-Assert (AAA)

## Test Organization

### CustomUserDetailService Tests (2 tests)

#### 1. testLoadUserByUsername_UserExists_ReturnsUserDetails
- **Purpose:** Verify user lookup and conversion to Spring Security `UserDetails`
- **Scenario:** Repository returns an existing user by username
- **Assertions:**
  - Username and encoded password are mapped correctly
  - Authority list contains exactly `ROLE_USER`
  - Repository lookup is invoked

#### 2. testLoadUserByUsername_UserMissing_ThrowsUsernameNotFoundException
- **Purpose:** Verify missing-user error path
- **Scenario:** Repository returns empty for username
- **Assertions:**
  - `UsernameNotFoundException` is thrown
  - Exception message equals `User not found`
  - Repository lookup is invoked

### SecurityConfig Tests (4 tests)

#### 3. testPasswordEncoder_ReturnsBCryptEncoder
- **Purpose:** Verify password encoder bean type
- **Scenario:** Request password encoder from config class
- **Assertions:**
  - Bean is `BCryptPasswordEncoder`

#### 4. testPasswordEncoder_EncodesAndMatchesRawPassword
- **Purpose:** Verify encoder behavior
- **Scenario:** Encode a raw password and validate with `matches`
- **Assertions:**
  - Encoded value differs from raw password
  - `matches` returns true for raw/encoded pair

#### 5. testSecurityFilterChain_BuildsAndReturnsFilterChain
- **Purpose:** Verify filter-chain construction path
- **Scenario:** Build security filter chain using mocked `HttpSecurity`
- **Assertions:**
  - Returned chain matches the result of `http.build()`

#### 6. testSecurityFilterChain_AppliesExpectedHttpSecurityBlocks
- **Purpose:** Verify all expected security config blocks are applied
- **Scenario:** Run `securityFilterChain` with mocked fluent `HttpSecurity`
- **Assertions:**
  - `authorizeHttpRequests` called once
  - `formLogin` called once
  - `logout` called once
  - `build` called once

## Key Testing Strategies

### 1. Layered Unit Testing
- `CustomUserDetailService` tests isolate repository behavior with Mockito
- `SecurityConfig` tests isolate HttpSecurity builder interactions with Mockito

### 2. Behavior-Focused Assertions
- Validate returned domain/security objects and mapped authorities
- Validate security builder interactions and final chain return

### 3. Delegation Verification
- Confirm repository lookup calls in user-details service
- Confirm core security configuration calls in config class

### 4. Consistent Style
- `@DisplayName` on all tests
- Section banners for readability
- Method names follow `testMethod_Scenario_Expected`

## Running the Tests

### Run all security package tests:
```bash
./mvnw test -Dtest=SecurityConfigTest,CustomUserDetailServiceTest
```

### Run one test class:
```bash
./mvnw test -Dtest=SecurityConfigTest
```

### Run one test method:
```bash
./mvnw test -Dtest=CustomUserDetailServiceTest#testLoadUserByUsername_UserMissing_ThrowsUsernameNotFoundException
```

## Test Results

✅ Security test suite passes successfully
- **Tests run:** 6
- **Failures:** 0
- **Errors:** 0
- **Skipped:** 0

## Coverage Summary

The suite covers:
- ✅ Custom user-details lookup success and failure paths
- ✅ Authority mapping to `ROLE_USER`
- ✅ BCrypt password encoder type and behavior
- ✅ Security filter-chain construction
- ✅ Core security configuration block application

## Future Enhancements
- Add endpoint-level authorization tests if web test auto-configuration support is added
- Add login-failure parameter handling tests
- Add role-based authorization tests if additional roles are introduced
