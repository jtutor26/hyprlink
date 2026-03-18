package com.basecamp.HyprLink.controller;

import com.basecamp.HyprLink.entity.SocialLink;
import com.basecamp.HyprLink.entity.User;
import com.basecamp.HyprLink.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("DashboardController Tests")
class DashboardControllerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private Principal principal;

    private DashboardController dashboardController;

    @BeforeEach
    void setUp() {
        dashboardController = new DashboardController(userRepository);
    }

    // ==================== Dashboard View Endpoint Tests ====================

    @Test
    @DisplayName("Should return dashboard view, append blank link, and populate model for GET /dashboard")
    void testShowDashboard_UserExists_PopulatesModelAndReturnsDashboardView() {
        // Arrange
        Model model = new ExtendedModelMap();
        User dashboardUser = new User();
        dashboardUser.setUsername("johndoe");

        SocialLink existingLink = new SocialLink();
        existingLink.setTitle("Portfolio");
        existingLink.setUrl("https://example.com");
        dashboardUser.setSocialLinks(new ArrayList<>(List.of(existingLink)));

        when(principal.getName()).thenReturn("johndoe");
        when(userRepository.findByUsername("johndoe")).thenReturn(Optional.of(dashboardUser));

        // Act
        String viewName = dashboardController.showDashboard(principal, model);

        // Assert
        assertThat(viewName).isEqualTo("dashboard");
        assertThat(model.getAttribute("user")).isSameAs(dashboardUser);
        assertThat(model.getAttribute("themes")).isEqualTo(List.of("default", "dark"));
        assertThat(model.getAttribute("linkStyles")).isEqualTo(List.of("pill", "box", "underline"));
        assertThat(model.getAttribute("textAlignments")).isEqualTo(List.of("center", "left"));
        assertThat(model.getAttribute("buttonColors")).isEqualTo(List.of("blue", "green", "red", "purple", "orange"));
        assertThat(model.getAttribute("fontFamilies")).isEqualTo(List.of("System", "Georgia", "Courier", "Arial"));
        assertThat(model.getAttribute("backgrounds")).isInstanceOf(List.class);
        assertThat(dashboardUser.getSocialLinks()).hasSize(2);
        assertThat(dashboardUser.getSocialLinks().get(1).getTitle()).isNull();
    }

    // ==================== Dashboard Save Endpoint Tests ====================

    @Test
    @DisplayName("Should save profile updates, filter blank links, and redirect for POST /dashboard/save")
    void testSaveProfile_ValidUpdates_FiltersLinksAndRedirects() throws IOException {
        // Arrange
        User existingUser = new User();
        existingUser.setUsername("johndoe");
        existingUser.setSocialLinks(new ArrayList<>(List.of(new SocialLink())));

        User updatedData = new User();
        updatedData.setName("New Name");
        updatedData.setAge("24");
        updatedData.setPronouns("they/them");
        updatedData.setBio("New bio");
        updatedData.setTheme("dark");
        updatedData.setLinkStyle("pill");
        updatedData.setTextAlign("center");
        updatedData.setButtonColor("purple");
        updatedData.setFontFamily("Georgia");
        updatedData.setProfilePicture("https://example.com/avatar.png");
        updatedData.setBackgroundImage("/images/background-templates/1.png");

        SocialLink validLink = new SocialLink();
        validLink.setTitle("GitHub");
        validLink.setUrl("https://github.com/johndoe");

        SocialLink blankLink = new SocialLink();
        blankLink.setTitle(" ");
        blankLink.setUrl(" ");

        updatedData.setSocialLinks(List.of(validLink, blankLink));

        when(principal.getName()).thenReturn("johndoe");
        when(userRepository.findByUsername("johndoe")).thenReturn(Optional.of(existingUser));

        // Act
        String redirect = dashboardController.saveProfile(updatedData, principal, null, null);

        // Assert
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User captured = userCaptor.getValue();
        assertThat(captured.getName()).isEqualTo("New Name");
        assertThat(captured.getBio()).isEqualTo("New bio");
        assertThat(captured.getTheme()).isEqualTo("dark");
        assertThat(captured.getSocialLinks()).hasSize(1);
        assertThat(captured.getSocialLinks().get(0).getTitle()).isEqualTo("GitHub");
        assertThat(redirect).isEqualTo("redirect:/dashboard?success");
    }

    // ==================== Exception Handler Tests ====================

    @Test
    @DisplayName("Should redirect to dashboard with uploadTooLarge flag when upload exceeds limit")
    void testHandleUploadTooLarge_RedirectsWithFlag() {
        // Act
        String redirect = dashboardController.handleUploadTooLarge();

        // Assert
        assertThat(redirect).isEqualTo("redirect:/dashboard?uploadTooLarge");
    }
}
