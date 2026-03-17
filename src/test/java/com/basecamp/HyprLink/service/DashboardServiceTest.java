package com.basecamp.HyprLink.service;

import com.basecamp.HyprLink.entity.SocialLink;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("DashboardService Tests")
class DashboardServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private DashboardService dashboardService;

    private User existingUser;

    @BeforeEach
    void setUp() {
        existingUser = new User();
        existingUser.setUsername("johndoe");
        existingUser.setName("Old Name");
        existingUser.setAge("30");
        existingUser.setPronouns("he/him");
        existingUser.setBio("Old bio");
        existingUser.setProfilePicture("old-pic");
        existingUser.setTheme("default");

        SocialLink existingLink = new SocialLink();
        existingLink.setTitle("Portfolio");
        existingLink.setUrl("https://example.com/portfolio");
        existingUser.setSocialLinks(new ArrayList<>(List.of(existingLink)));
    }

    // ==================== Dashboard Retrieval Tests ====================

    @Test
    @DisplayName("Should return user and append a blank social link for dashboard")
    void testGetUserForDashboard_UserExists_AppendsBlankLink() {
        // Arrange
        when(userRepository.findByUsername("johndoe")).thenReturn(Optional.of(existingUser));

        // Act
        User result = dashboardService.getUserForDashboard("johndoe");

        // Assert
        assertThat(result).isSameAs(existingUser);
        assertThat(result.getSocialLinks()).hasSize(2);
        SocialLink appendedLink = result.getSocialLinks().get(1);
        assertThat(appendedLink.getTitle()).isNull();
        assertThat(appendedLink.getUrl()).isNull();
    }

    @Test
    @DisplayName("Should return null when dashboard user is not found")
    void testGetUserForDashboard_UserNotFound_ReturnsNull() {
        // Arrange
        when(userRepository.findByUsername("missing-user")).thenReturn(Optional.empty());

        // Act
        User result = dashboardService.getUserForDashboard("missing-user");

        // Assert
        assertThat(result).isNull();
    }

    // ==================== Profile Update Tests ====================

    @Test
    @DisplayName("Should update profile fields, filter blank links, and save user")
    void testUpdateUserProfile_UpdatesFieldsFiltersLinksAndSaves() {
        // Arrange
        User updatedData = new User();
        updatedData.setName("New Name");
        updatedData.setAge("25");
        updatedData.setPronouns("they/them");
        updatedData.setBio("New bio");
        updatedData.setProfilePicture("new-pic");
        updatedData.setTheme("default");

        SocialLink validLink = new SocialLink();
        validLink.setTitle("GitHub");
        validLink.setUrl("https://github.com/johndoe");

        SocialLink blankTitle = new SocialLink();
        blankTitle.setTitle(" ");
        blankTitle.setUrl("https://example.com");

        SocialLink blankUrl = new SocialLink();
        blankUrl.setTitle("LinkedIn");
        blankUrl.setUrl(" ");

        SocialLink nullFields = new SocialLink();
        nullFields.setTitle(null);
        nullFields.setUrl(null);

        updatedData.setSocialLinks(List.of(validLink, blankTitle, blankUrl, nullFields));

        when(userRepository.findByUsername("johndoe")).thenReturn(Optional.of(existingUser));
        when(userRepository.save(existingUser)).thenReturn(existingUser);

        // Act
        User result = dashboardService.updateUserProfile(updatedData, "johndoe");

        // Assert
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertThat(result).isSameAs(existingUser);
        assertThat(savedUser.getName()).isEqualTo("New Name");
        assertThat(savedUser.getAge()).isEqualTo("25");
        assertThat(savedUser.getPronouns()).isEqualTo("they/them");
        assertThat(savedUser.getBio()).isEqualTo("New bio");
        assertThat(savedUser.getProfilePicture()).isEqualTo("new-pic");
        assertThat(savedUser.getTheme()).isEqualTo("default");
        assertThat(savedUser.getSocialLinks()).hasSize(1);
        assertThat(savedUser.getSocialLinks().get(0).getTitle()).isEqualTo("GitHub");
        assertThat(savedUser.getSocialLinks().get(0).getUrl()).isEqualTo("https://github.com/johndoe");
    }

    @Test
    @DisplayName("Should keep existing links when updated data has null social links")
    void testUpdateUserProfile_NullSocialLinks_KeepsExistingLinks() {
        // Arrange
        User updatedData = new User();
        updatedData.setName("New Name");
        updatedData.setAge("27");
        updatedData.setPronouns("they/them");
        updatedData.setBio("Updated bio");
        updatedData.setProfilePicture("new-avatar");
        updatedData.setTheme("default");
        updatedData.setSocialLinks(null);

        List<SocialLink> originalLinks = new ArrayList<>(existingUser.getSocialLinks());

        when(userRepository.findByUsername("johndoe")).thenReturn(Optional.of(existingUser));
        when(userRepository.save(existingUser)).thenReturn(existingUser);

        // Act
        dashboardService.updateUserProfile(updatedData, "johndoe");

        // Assert
        assertThat(existingUser.getName()).isEqualTo("New Name");
        assertThat(existingUser.getAge()).isEqualTo("27");
        assertThat(existingUser.getPronouns()).isEqualTo("they/them");
        assertThat(existingUser.getBio()).isEqualTo("Updated bio");
        assertThat(existingUser.getProfilePicture()).isEqualTo("new-avatar");
        assertThat(existingUser.getSocialLinks()).hasSameSizeAs(originalLinks);
        assertThat(existingUser.getSocialLinks().get(0).getTitle()).isEqualTo("Portfolio");
        verify(userRepository).save(existingUser);
    }

    @Test
    @DisplayName("Should clear existing links when updated data contains only blank links")
    void testUpdateUserProfile_OnlyBlankLinks_ClearsExistingLinks() {
        // Arrange
        User updatedData = new User();

        SocialLink blankOne = new SocialLink();
        blankOne.setTitle(" ");
        blankOne.setUrl(" ");

        SocialLink blankTwo = new SocialLink();
        blankTwo.setTitle("");
        blankTwo.setUrl("");

        updatedData.setSocialLinks(List.of(blankOne, blankTwo));

        when(userRepository.findByUsername("johndoe")).thenReturn(Optional.of(existingUser));
        when(userRepository.save(existingUser)).thenReturn(existingUser);

        // Act
        User result = dashboardService.updateUserProfile(updatedData, "johndoe");

        // Assert
        assertThat(result).isSameAs(existingUser);
        assertThat(existingUser.getSocialLinks()).isEmpty();
        verify(userRepository).save(existingUser);
    }

    @Test
    @DisplayName("Should throw exception when updating profile for missing user")
    void testUpdateUserProfile_UserNotFound_ThrowsException() {
        // Arrange
        User updatedData = new User();
        when(userRepository.findByUsername("missing-user")).thenReturn(Optional.empty());

        // Act + Assert
        assertThatThrownBy(() -> dashboardService.updateUserProfile(updatedData, "missing-user"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("User not found: missing-user");
    }

    // ==================== Theme Tests ====================

    @Test
    @DisplayName("Should return available themes")
    void testGetAvailableThemes_ReturnsDefaultTheme() {
        // Act
        List<String> themes = dashboardService.getAvailableThemes();

        // Assert
        assertThat(themes).containsExactly("default");
    }
}


