package com.basecamp.HyprLink.controller;

import com.basecamp.HyprLink.entity.User;
import com.basecamp.HyprLink.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthController Tests")
class AuthControllerTest {

    @Mock
    private AuthService authService;

    private AuthController authController;

    @BeforeEach
    void setUp() {
        authController = new AuthController(authService);
    }

    // ==================== Login Endpoint Tests ====================

    @Test
    @DisplayName("Should return login view for GET /login")
    void testShowLoginForm_ReturnsLoginView() {
        // Act
        String viewName = authController.showLoginForm();

        // Assert
        assertThat(viewName).isEqualTo("auth/login");
    }

    // ==================== Registration Page Endpoint Tests ====================

    @Test
    @DisplayName("Should return register view and populate model for GET /register")
    void testShowRegistrationForm_PopulatesModelAndReturnsRegisterView() {
        // Arrange
        Model model = new ExtendedModelMap();
        User formUser = new User();
        List<String> themes = List.of("default");

        when(authService.prepareRegistrationFormData()).thenReturn(formUser);
        when(authService.getAvailableThemes()).thenReturn(themes);

        // Act
        String viewName = authController.showRegistrationForm(model);

        // Assert
        assertThat(viewName).isEqualTo("auth/register");
        assertThat(model.getAttribute("user")).isSameAs(formUser);
        assertThat(model.getAttribute("themes")).isEqualTo(themes);
        verify(authService).prepareRegistrationFormData();
        verify(authService).getAvailableThemes();
    }

    // ==================== Registration Submit Endpoint Tests ====================

    @Test
    @DisplayName("Should register user and redirect to login success for POST /register")
    void testRegisterUser_CallsServiceAndRedirects() {
        // Arrange
        User incomingUser = new User();
        incomingUser.setUsername("johndoe");
        incomingUser.setPassword("plain-password");

        // Act
        String redirect = authController.registerUser(incomingUser);

        // Assert
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(authService).registerUser(userCaptor.capture());

        User captured = userCaptor.getValue();
        assertThat(captured.getUsername()).isEqualTo("johndoe");
        assertThat(captured.getPassword()).isEqualTo("plain-password");
        assertThat(redirect).isEqualTo("redirect:/login?success");
    }
}

