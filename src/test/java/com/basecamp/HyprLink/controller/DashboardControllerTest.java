package com.basecamp.HyprLink.controller;

import com.basecamp.HyprLink.entity.User;
import com.basecamp.HyprLink.service.DashboardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import java.security.Principal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("DashboardController Tests")
class DashboardControllerTest {

    @Mock
    private DashboardService dashboardService;

    @Mock
    private Principal principal;

    private DashboardController dashboardController;

    @BeforeEach
    void setUp() {
        dashboardController = new DashboardController(dashboardService);
    }

    // ==================== Dashboard View Endpoint Tests ====================

    @Test
    @DisplayName("Should return dashboard view and populate model for GET /dashboard")
    void testShowDashboard_PopulatesModelAndReturnsDashboardView() {
        // Arrange
        Model model = new ExtendedModelMap();
        User dashboardUser = new User();
        dashboardUser.setUsername("johndoe");
        List<String> themes = List.of("default");

        when(principal.getName()).thenReturn("johndoe");
        when(dashboardService.getUserForDashboard("johndoe")).thenReturn(dashboardUser);
        when(dashboardService.getAvailableThemes()).thenReturn(themes);

        // Act
        String viewName = dashboardController.showDashboard(principal, model);

        // Assert
        assertThat(viewName).isEqualTo("dashboard");
        assertThat(model.getAttribute("user")).isSameAs(dashboardUser);
        assertThat(model.getAttribute("themes")).isEqualTo(themes);
        verify(dashboardService).getUserForDashboard("johndoe");
        verify(dashboardService).getAvailableThemes();
    }

    @Test
    @DisplayName("Should return dashboard view with null user when service does not find user")
    void testShowDashboard_UserMissing_ReturnsDashboardWithNullUser() {
        // Arrange
        Model model = new ExtendedModelMap();

        when(principal.getName()).thenReturn("missing-user");
        when(dashboardService.getUserForDashboard("missing-user")).thenReturn(null);
        when(dashboardService.getAvailableThemes()).thenReturn(List.of("default"));

        // Act
        String viewName = dashboardController.showDashboard(principal, model);

        // Assert
        assertThat(viewName).isEqualTo("dashboard");
        assertThat(model.getAttribute("user")).isNull();
        assertThat(model.getAttribute("themes")).isEqualTo(List.of("default"));
    }

    // ==================== Dashboard Save Endpoint Tests ====================

    @Test
    @DisplayName("Should save profile and redirect for POST /dashboard/save")
    void testSaveProfile_CallsServiceAndRedirects() {
        // Arrange
        User updatedData = new User();
        updatedData.setName("New Name");
        updatedData.setBio("New bio");

        when(principal.getName()).thenReturn("johndoe");

        // Act
        String redirect = dashboardController.saveProfile(updatedData, principal);

        // Assert
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(dashboardService).updateUserProfile(userCaptor.capture(), org.mockito.ArgumentMatchers.eq("johndoe"));

        User captured = userCaptor.getValue();
        assertThat(captured.getName()).isEqualTo("New Name");
        assertThat(captured.getBio()).isEqualTo("New bio");
        assertThat(redirect).isEqualTo("redirect:/dashboard?success");
    }
}

