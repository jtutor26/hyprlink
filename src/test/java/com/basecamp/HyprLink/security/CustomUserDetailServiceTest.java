package com.basecamp.HyprLink.security;

import com.basecamp.HyprLink.entity.User;
import com.basecamp.HyprLink.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("CustomUserDetailService Tests")
class CustomUserDetailServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailService customUserDetailService;

    // ==================== User Details Lookup Tests ====================

    @Test
    @DisplayName("Should return Spring Security user details when username exists")
    void testLoadUserByUsername_UserExists_ReturnsUserDetails() {
        // Arrange
        User entityUser = new User();
        entityUser.setUsername("johndoe");
        entityUser.setPassword("encoded-password");

        when(userRepository.findByUsername("johndoe")).thenReturn(Optional.of(entityUser));

        // Act
        UserDetails userDetails = customUserDetailService.loadUserByUsername("johndoe");

        // Assert
        assertThat(userDetails.getUsername()).isEqualTo("johndoe");
        assertThat(userDetails.getPassword()).isEqualTo("encoded-password");
        assertThat(userDetails.getAuthorities())
                .extracting("authority")
                .containsExactly("ROLE_USER");
        verify(userRepository).findByUsername("johndoe");
    }

    @Test
    @DisplayName("Should throw UsernameNotFoundException when username does not exist")
    void testLoadUserByUsername_UserMissing_ThrowsUsernameNotFoundException() {
        // Arrange
        when(userRepository.findByUsername("missing-user")).thenReturn(Optional.empty());

        // Act + Assert
        assertThatThrownBy(() -> customUserDetailService.loadUserByUsername("missing-user"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("User not found");
        verify(userRepository).findByUsername("missing-user");
    }
}

