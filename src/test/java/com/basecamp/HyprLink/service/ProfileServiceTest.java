package com.basecamp.HyprLink.service;

import com.basecamp.HyprLink.entity.User;
import com.basecamp.HyprLink.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProfileService Tests")
class ProfileServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ProfileService profileService;

    // ==================== Profile Lookup Tests ====================

    @Test
    @DisplayName("Should return user profile when user ID exists")
    void testGetUserProfileById_UserExists_ReturnsUser() {
        // Arrange
        User user = new User();
        user.setId(1L);
        user.setUsername("johndoe");
        user.setName("John Doe");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Act
        User result = profileService.getUserProfileById(1L);

        // Assert
        assertThat(result).isSameAs(user);
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getUsername()).isEqualTo("johndoe");
        verify(userRepository).findById(1L);
    }

    @Test
    @DisplayName("Should return null when user ID does not exist")
    void testGetUserProfileById_UserMissing_ReturnsNull() {
        // Arrange
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        User result = profileService.getUserProfileById(999L);

        // Assert
        assertThat(result).isNull();
        verify(userRepository).findById(999L);
    }

    @Test
    @DisplayName("Should return null when repository has no user for null ID")
    void testGetUserProfileById_NullId_ReturnsNull() {
        // Arrange
        when(userRepository.findById(null)).thenReturn(Optional.empty());

        // Act
        User result = profileService.getUserProfileById(null);

        // Assert
        assertThat(result).isNull();
        verify(userRepository).findById(null);
    }
}


