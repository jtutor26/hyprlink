# UserRepository Test Suite Documentation

## Overview
The `UserRepositoryTest` class provides comprehensive test coverage for the `UserRepository` interface, testing both custom query methods and inherited JPA methods. The test suite includes 17 test cases covering CRUD operations, custom queries, edge cases, and data validation.

## Test Configuration
- **Framework:** JUnit 5 (Jupiter)
- **Testing Library:** AssertJ (Fluent Assertions)
- **Spring Boot Testing:** @SpringBootTest
- **Database:** H2 (in-memory for testing)
- **Transaction Management:** @Transactional (auto-rollback between tests)
- **Test Profile:** @ActiveProfiles("test")

## Test Organization

### Custom Query Tests (4 tests)

#### 1. testFindByUsername_Success
- **Purpose:** Verify that a user can be found by exact username match
- **Scenario:** Save a user, then retrieve it by username
- **Assertions:**
  - User is present in Optional
  - Username matches
  - Associated data (name, age) is correctly retrieved

#### 2. testFindByUsername_NotFound
- **Purpose:** Verify that non-existent usernames return empty Optional
- **Scenario:** Query for a username that doesn't exist
- **Assertions:**
  - Optional is empty
  - No exception is thrown

#### 3. testFindByUsername_CaseSensitive
- **Purpose:** Verify that username search is case-sensitive
- **Scenario:** Save user "johndoe1", search for "Johndoe1" (different case)
- **Assertions:**
  - Optional is empty (case mismatch results in no match)

#### 4. testFindByUsername_MultipleUsers
- **Purpose:** Verify correct user is returned when multiple users exist
- **Scenario:** Create multiple users, query for specific user
- **Assertions:**
  - Correct user is returned with matching data
  - Other users are not returned

### JpaRepository Inherited Tests (10 tests)

#### 5. testSave_NewUser
- **Purpose:** Verify that a new user can be saved to the database
- **Scenario:** Save a new User entity
- **Assertions:**
  - User receives an ID after save
  - All fields are persisted correctly

#### 6. testSave_UserWithSocialLinks
- **Purpose:** Verify that cascading save works for associated SocialLinks
- **Scenario:** Save user with 2 social links
- **Assertions:**
  - User and all social links are persisted
  - Links can be retrieved with user data
  - Link count and data match what was saved

#### 7. testFindById_Success
- **Purpose:** Verify user retrieval by primary key
- **Scenario:** Save user, then find by ID
- **Assertions:**
  - User is found
  - ID matches
  - All user data is correct

#### 8. testFindById_NotFound
- **Purpose:** Verify that invalid IDs return empty Optional
- **Scenario:** Query for non-existent ID
- **Assertions:**
  - Optional is empty

#### 9. testFindAll
- **Purpose:** Verify retrieval of all users in database
- **Scenario:** Create 3 users, retrieve all
- **Assertions:**
  - All 3 users are returned
  - All usernames are present

#### 10. testSave_UpdateExistingUser
- **Purpose:** Verify that existing users can be updated
- **Scenario:** Save user, modify fields, save again
- **Assertions:**
  - ID remains the same
  - Updated fields reflect new values
  - Update is persisted

#### 11. testDeleteById
- **Purpose:** Verify deletion by ID
- **Scenario:** Save user, delete by ID
- **Assertions:**
  - User no longer exists in database
  - Subsequent query returns empty Optional

#### 12. testDelete
- **Purpose:** Verify deletion of entity
- **Scenario:** Save user, delete entity
- **Assertions:**
  - User no longer exists
  - ID cannot be found

#### 13. testExistsById
- **Purpose:** Verify existence checking
- **Scenario:** Check if saved user exists, and non-existent user doesn't
- **Assertions:**
  - Existing user returns true
  - Non-existent ID returns false

#### 14. testCount
- **Purpose:** Verify counting total users
- **Scenario:** Create 2 users, count all
- **Assertions:**
  - Count is at least 2

### Edge Cases & Data Integrity Tests (3 tests)

#### 15. testSave_NullSocialLinks
- **Purpose:** Verify that users can be saved with null social links
- **Scenario:** Save user with socialLinks = null
- **Assertions:**
  - User saves successfully
  - ID is generated

#### 16. testSave_EmptySocialLinks
- **Purpose:** Verify that users can be saved with empty social links list
- **Scenario:** Save user with empty ArrayList of links
- **Assertions:**
  - User persists
  - Links list is empty on retrieval

#### 17. testUpdate_SocialLinks
- **Purpose:** Verify that social links can be added to existing user
- **Scenario:** Save user, add new social link, save again
- **Assertions:**
  - New link is persisted
  - All 3 links exist after update
  - Link titles are correct

## Key Testing Strategies

### 1. Data Isolation
- Each test calls `deleteAll()` in `@BeforeEach` to ensure clean state
- Unique usernames generated using test counter to prevent constraint violations

### 2. Transaction Management
- `@Transactional` annotation auto-rolls back changes after each test
- `entityManager.flush()` forces persistence operations
- `entityManager.clear()` clears the persistence context to verify database state

### 3. Assertion Patterns
- **AssertJ Fluent API** for readable assertions
- **Lambda-based assertions** for complex validations
- **Exact value checks** for critical fields
- **Collection assertions** for related data

### 4. Test Data
- Consistent test user created in `@BeforeEach`
- Sample social links with realistic data
- Clear field values for easy debugging

## Running the Tests

### Run all UserRepository tests:
```bash
./mvnw test -Dtest=UserRepositoryTest
```

### Run a specific test:
```bash
./mvnw test -Dtest=UserRepositoryTest#testFindByUsername_Success
```

### Run all tests (including others):
```bash
./mvnw test
```

## Test Results

✅ All 17 tests pass successfully
- 0 failures
- 0 skipped
- Average execution time: ~2.4 seconds
- Database: H2 in-memory

## Code Coverage

The test suite covers:
- ✅ Custom finder method (`findByUsername`)
- ✅ CRUD operations (Create, Read, Update, Delete)
- ✅ Query methods (findById, findAll, existsById, count)
- ✅ Cascading relationships (User → SocialLinks)
- ✅ Edge cases (null values, empty collections)
- ✅ Data integrity (persistence, retrieval)

## Best Practices Demonstrated

1. **Descriptive Test Names** - Test method names clearly describe what is being tested
2. **@DisplayName Annotations** - Provides human-readable test descriptions
3. **Arrange-Act-Assert Pattern** - Clear test structure with setup, execution, verification
4. **Single Responsibility** - Each test focuses on one behavior
5. **No Test Dependencies** - Tests run independently in any order
6. **Meaningful Assertions** - Clear messages when assertions fail
7. **Cleanup** - Each test starts with a clean database state

## Future Enhancements

Potential additions to expand test coverage:
- Test for duplicate username constraint violations
- Test for required field validation (null username/password)
- Test for password field constraints
- Performance tests for large datasets
- Transaction isolation level testing
- Custom query with multiple parameters (if added)

