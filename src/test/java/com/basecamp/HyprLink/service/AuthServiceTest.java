package com.basecamp.HyprLink.service;

import com.basecamp.HyprLink.entity.User;
import com.basecamp.HyprLink.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService Tests")
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUsername("johndoe");
        testUser.setPassword("plain-password");
        testUser.setName("John Doe");
    }

    // ==================== Registration Tests ====================

    @Test
    @DisplayName("Should encode password and save user when registering")
    void testRegisterUser_EncodesPasswordAndSaves() {
        // Arrange
        when(passwordEncoder.encode("plain-password")).thenReturn("encoded-password");
        when(userRepository.save(testUser)).thenReturn(testUser);

        // Act
        authService.registerUser(testUser);

        // Assert
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User capturedUser = userCaptor.getValue();
        assertThat(capturedUser.getPassword()).isEqualTo("encoded-password");
        assertThat(capturedUser.getUsername()).isEqualTo("johndoe");
    }

    @Test
    @DisplayName("Should return saved user when registration succeeds")
    void testRegisterUser_ReturnsSavedUser() {
        // Arrange
        when(passwordEncoder.encode("plain-password")).thenReturn("encoded-password");
        when(userRepository.save(testUser)).thenReturn(testUser);

        // Act
        User savedUser = authService.registerUser(testUser);

        // Assert
        assertThat(savedUser).isSameAs(testUser);
        verify(passwordEncoder).encode("plain-password");
        verify(userRepository).save(testUser);
    }

    // ==================== Registration Form Initialization Tests ====================

    @Test
    @DisplayName("Should prepare registration data with one blank social link")
    void testPrepareRegistrationFormData_InitializesBlankLink() {
        // Act
        User preparedUser = authService.prepareRegistrationFormData();

        // Assert
        assertThat(preparedUser).isNotNull();
        assertThat(preparedUser.getSocialLinks()).isNotNull();
        assertThat(preparedUser.getSocialLinks()).hasSize(1);
        assertThat(preparedUser.getSocialLinks().get(0).getTitle()).isNull();
        assertThat(preparedUser.getSocialLinks().get(0).getUrl()).isNull();
    }

    @Test
    @DisplayName("Should return a fresh user model each time registration data is prepared")
    void testPrepareRegistrationFormData_ReturnsFreshInstancePerCall() {
        // Act
        User first = authService.prepareRegistrationFormData();
        User second = authService.prepareRegistrationFormData();

        // Assert
        assertThat(first).isNotSameAs(second);
        assertThat(first.getSocialLinks()).isNotSameAs(second.getSocialLinks());
        assertThat(first.getSocialLinks()).hasSize(1);
        assertThat(second.getSocialLinks()).hasSize(1);
    }

    // ==================== Theme Tests ====================

    @Test
    @DisplayName("Should return available themes")
    void testGetAvailableThemes_ReturnsDefaultTheme() {
        // Act
        java.util.List<String> themes = authService.getAvailableThemes();

        // Assert
        assertThat(themes).containsExactly("default");
    }
}


