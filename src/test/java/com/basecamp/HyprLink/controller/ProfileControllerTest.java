package com.basecamp.HyprLink.controller;

import com.basecamp.HyprLink.entity.User;
import com.basecamp.HyprLink.service.ProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProfileController Tests")
class ProfileControllerTest {

    @Mock
    private ProfileService profileService;

    private ProfileController profileController;

    @BeforeEach
    void setUp() {
        profileController = new ProfileController(profileService);
    }

    // ==================== Public Profile Endpoint Tests ====================

    @Test
    @DisplayName("Should return profile view and add user to model for GET /profile/{id}")
    void testGetProfile_UserExists_ReturnsProfileView() {
        // Arrange
        Model model = new ExtendedModelMap();
        User user = new User();
        user.setId(1L);
        user.setUsername("johndoe");
        user.setName("John Doe");

        when(profileService.getUserProfileById(1L)).thenReturn(user);

        // Act
        String viewName = profileController.getProfileById(1L, model);

        // Assert
        assertThat(viewName).isEqualTo("profile");
        assertThat(model.getAttribute("user")).isSameAs(user);
        verify(profileService).getUserProfileById(1L);
    }

    @Test
    @DisplayName("Should return 404 view when profile is not found for GET /profile/{id}")
    void testGetProfile_UserMissing_Returns404View() {
        // Arrange
        Model model = new ExtendedModelMap();
        when(profileService.getUserProfileById(999L)).thenReturn(null);

        // Act
        String viewName = profileController.getProfileById(999L, model);

        // Assert
        assertThat(viewName).isEqualTo("error/404");
        assertThat(model.containsAttribute("user")).isFalse();
        verify(profileService).getUserProfileById(999L);
    }

    @Test
    @DisplayName("Should return 404 view when profile lookup is called with null id")
    void testGetProfile_NullId_Returns404View() {
        // Arrange
        Model model = new ExtendedModelMap();
        when(profileService.getUserProfileById(null)).thenReturn(null);

        // Act
        String viewName = profileController.getProfileById(null, model);

        // Assert
        assertThat(viewName).isEqualTo("error/404");
        assertThat(model.containsAttribute("user")).isFalse();
        verify(profileService).getUserProfileById(null);
    }
}

