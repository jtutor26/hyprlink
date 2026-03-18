package com.basecamp.HyprLink.repository;

import com.basecamp.HyprLink.entity.SocialLink;
import com.basecamp.HyprLink.entity.User;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@DisplayName("UserRepository Tests")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager entityManager;

    private User testUser;
    private static int testCounter = 0;

    @BeforeEach
    void setUp() {
        // Clear data before each test to avoid unique constraint violations
        testCounter++;
        userRepository.deleteAll();
        entityManager.flush();

        // Create a test user with sample data using unique username
        testUser = new User();
        testUser.setUsername("johndoe" + testCounter);
        testUser.setPassword("hashedpassword123");
        testUser.setName("John Doe");
        testUser.setAge("30");
        testUser.setPronouns("he/him");
        testUser.setBio("Test user bio");
        testUser.setProfilePicture("https://example.com/avatar.jpg");
        testUser.setTheme("default");

        // Add sample social links
        SocialLink link1 = new SocialLink();
        link1.setTitle("Portfolio");
        link1.setUrl("https://example.com/portfolio");

        SocialLink link2 = new SocialLink();
        link2.setTitle("Twitter");
        link2.setUrl("https://twitter.com/johndoe");

        testUser.setSocialLinks(new ArrayList<>(List.of(link1, link2)));
    }

    // ==================== Custom Query Tests ====================

    @Test
    @DisplayName("Should find user by username when user exists")
    void testFindByUsername_Success() {
        // Arrange
        userRepository.save(testUser);
        entityManager.flush();

        // Act
        Optional<User> foundUser = userRepository.findByUsername(testUser.getUsername());

        // Assert
        assertThat(foundUser)
                .isPresent()
                .hasValueSatisfying(user -> {
                    assertThat(user.getUsername()).isEqualTo(testUser.getUsername());
                    assertThat(user.getName()).isEqualTo("John Doe");
                    assertThat(user.getAge()).isEqualTo("30");
                });
    }

    @Test
    @DisplayName("Should return empty Optional when user does not exist")
    void testFindByUsername_NotFound() {
        // Act
        Optional<User> foundUser = userRepository.findByUsername("nonexistentuser");

        // Assert
        assertThat(foundUser).isEmpty();
    }

    @Test
    @DisplayName("Should be case-sensitive when finding by username")
    void testFindByUsername_CaseSensitive() {
        // Arrange
        userRepository.save(testUser);
        entityManager.flush();

        // Act - Try with different case
        String username = testUser.getUsername();
        String differentCaseUsername = username.substring(0, 1).toUpperCase() + username.substring(1);
        Optional<User> foundUser = userRepository.findByUsername(differentCaseUsername);

        // Assert
        assertThat(foundUser).isEmpty();
    }

    @Test
    @DisplayName("Should return correct user when multiple users exist")
    void testFindByUsername_MultipleUsers() {
        // Arrange
        User user1 = new User();
        user1.setUsername("alice" + testCounter);
        user1.setPassword("password123");
        user1.setName("Alice");

        User user2 = new User();
        user2.setUsername("bob" + testCounter);
        user2.setPassword("password456");
        user2.setName("Bob");

        userRepository.save(user1);
        userRepository.save(user2);
        entityManager.flush();

        // Act
        Optional<User> foundUser = userRepository.findByUsername(user1.getUsername());

        // Assert
        assertThat(foundUser)
                .isPresent()
                .hasValueSatisfying(user -> {
                    assertThat(user.getName()).isEqualTo("Alice");
                    assertThat(user.getUsername()).isEqualTo(user1.getUsername());
                });
    }

    // ==================== JpaRepository Inherited Tests ====================

    @Test
    @DisplayName("Should save a new user to database")
    void testSave_NewUser() {
        // Act
        User savedUser = userRepository.save(testUser);
        entityManager.flush();

        // Assert
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getUsername()).isEqualTo(testUser.getUsername());
        assertThat(savedUser.getName()).isEqualTo("John Doe");
    }

    @Test
    @DisplayName("Should save user with associated social links")
    void testSave_UserWithSocialLinks() {
        // Act
        User savedUser = userRepository.save(testUser);
        entityManager.flush();
        entityManager.clear();

        // Retrieve to verify persistence
        Optional<User> retrievedUser = userRepository.findById(savedUser.getId());

        // Assert
        assertThat(retrievedUser)
                .isPresent()
                .hasValueSatisfying(user -> {
                    assertThat(user.getSocialLinks()).hasSize(2);
                    assertThat(user.getSocialLinks())
                            .extracting(SocialLink::getTitle)
                            .containsExactly("Portfolio", "Twitter");
                });
    }

    @Test
    @DisplayName("Should find user by ID")
    void testFindById_Success() {
        // Arrange
        User savedUser = userRepository.save(testUser);
        entityManager.flush();

        // Act
        Optional<User> foundUser = userRepository.findById(savedUser.getId());

        // Assert
        assertThat(foundUser)
                .isPresent()
                .hasValueSatisfying(user -> {
                    assertThat(user.getId()).isEqualTo(savedUser.getId());
                    assertThat(user.getUsername()).isEqualTo(testUser.getUsername());
                });
    }

    @Test
    @DisplayName("Should return empty Optional when finding user by non-existent ID")
    void testFindById_NotFound() {
        // Act
        Optional<User> foundUser = userRepository.findById(9999L);

        // Assert
        assertThat(foundUser).isEmpty();
    }

    @Test
    @DisplayName("Should find all users in database")
    void testFindAll() {
        // Arrange
        User user1 = new User();
        user1.setUsername("user1" + testCounter);
        user1.setPassword("pass1");

        User user2 = new User();
        user2.setUsername("user2" + testCounter);
        user2.setPassword("pass2");

        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(testUser);
        entityManager.flush();

        // Act
        List<User> allUsers = userRepository.findAll();

        // Assert
        assertThat(allUsers).hasSize(3);
        assertThat(allUsers)
                .extracting(User::getUsername)
                .containsExactlyInAnyOrder(user1.getUsername(), user2.getUsername(), testUser.getUsername());
    }

    @Test
    @DisplayName("Should update existing user")
    void testSave_UpdateExistingUser() {
        // Arrange
        User savedUser = userRepository.save(testUser);
        entityManager.flush();

        // Act - Update the user
        savedUser.setName("Jane Doe");
        savedUser.setAge("25");
        User updatedUser = userRepository.save(savedUser);
        entityManager.flush();

        // Assert
        assertThat(updatedUser.getId()).isEqualTo(savedUser.getId());
        assertThat(updatedUser.getName()).isEqualTo("Jane Doe");
        assertThat(updatedUser.getAge()).isEqualTo("25");
    }

    @Test
    @DisplayName("Should delete user by ID")
    void testDeleteById() {
        // Arrange
        User savedUser = userRepository.save(testUser);
        entityManager.flush();
        Long userId = savedUser.getId();

        // Act
        userRepository.deleteById(userId);
        entityManager.flush();

        // Assert
        Optional<User> deletedUser = userRepository.findById(userId);
        assertThat(deletedUser).isEmpty();
    }

    @Test
    @DisplayName("Should delete user entity")
    void testDelete() {
        // Arrange
        User savedUser = userRepository.save(testUser);
        entityManager.flush();

        // Act
        userRepository.delete(savedUser);
        entityManager.flush();

        // Assert
        Optional<User> deletedUser = userRepository.findById(savedUser.getId());
        assertThat(deletedUser).isEmpty();
    }

    @Test
    @DisplayName("Should check if user exists by ID")
    void testExistsById() {
        // Arrange
        User savedUser = userRepository.save(testUser);
        entityManager.flush();

        // Act & Assert
        assertThat(userRepository.existsById(savedUser.getId())).isTrue();
        assertThat(userRepository.existsById(9999L)).isFalse();
    }

    @Test
    @DisplayName("Should count total users in database")
    void testCount() {
        // Arrange
        userRepository.save(testUser);
        User user2 = new User();
        user2.setUsername("anotheruser" + testCounter);
        user2.setPassword("pass123");
        userRepository.save(user2);
        entityManager.flush();

        // Act
        long count = userRepository.count();

        // Assert
        assertThat(count).isGreaterThanOrEqualTo(2);
    }

    // ==================== Edge Cases & Validation ====================

    @Test
    @DisplayName("Should handle null social links list")
    void testSave_NullSocialLinks() {
        // Arrange
        testUser.setSocialLinks(null);

        // Act
        User savedUser = userRepository.save(testUser);
        entityManager.flush();

        // Assert
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getUsername()).isEqualTo(testUser.getUsername());
    }

    @Test
    @DisplayName("Should persist user with empty social links")
    void testSave_EmptySocialLinks() {
        // Arrange
        testUser.setSocialLinks(List.of());

        // Act
        User savedUser = userRepository.save(testUser);
        entityManager.flush();
        entityManager.clear();

        // Retrieve to verify
        Optional<User> retrievedUser = userRepository.findById(savedUser.getId());

        // Assert
        assertThat(retrievedUser)
                .isPresent()
                .hasValueSatisfying(user -> {
                    assertThat(user.getSocialLinks()).isEmpty();
                });
    }

    @Test
    @DisplayName("Should update user social links")
    void testUpdate_SocialLinks() {
        // Arrange
        User savedUser = userRepository.save(testUser);
        entityManager.flush();

        // Act - Add a new link
        SocialLink newLink = new SocialLink();
        newLink.setTitle("GitHub");
        newLink.setUrl("https://github.com/johndoe");
        savedUser.getSocialLinks().add(newLink);
        userRepository.save(savedUser);
        entityManager.flush();
        entityManager.clear();

        // Retrieve to verify
        Optional<User> retrievedUser = userRepository.findById(savedUser.getId());

        // Assert
        assertThat(retrievedUser)
                .isPresent()
                .hasValueSatisfying(user -> {
                    assertThat(user.getSocialLinks()).hasSize(3);
                    assertThat(user.getSocialLinks())
                            .extracting(SocialLink::getTitle)
                            .containsExactly("Portfolio", "Twitter", "GitHub");
                });
    }
}