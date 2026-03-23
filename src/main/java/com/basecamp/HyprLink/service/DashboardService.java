package com.basecamp.HyprLink.service;

import com.basecamp.HyprLink.entity.SocialLink;
import com.basecamp.HyprLink.entity.User;
import com.basecamp.HyprLink.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DashboardService {

    private final UserRepository userRepository;

    public DashboardService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    public User getUserForDashboard(String username) {
        User user = userRepository.findByUsername(username).orElse(null);
        // Add a blank link slot at the end so the user always has room to add a new one
        if (user != null) {
            user.getSocialLinks().add(new SocialLink());
        }
        return user;
    }

    public User updateUserProfile(User updatedData, String username) {
        User existingUser = userRepository.findByUsername(username).orElseThrow(
                () -> new RuntimeException("User not found: " + username)
        );

        // Update basic fields
        existingUser.setName(updatedData.getName());
        existingUser.setAge(updatedData.getAge());
        existingUser.setPronouns(updatedData.getPronouns());
        existingUser.setBio(updatedData.getBio());
        existingUser.setProfilePicture(updatedData.getProfilePicture());
        existingUser.setTheme(updatedData.getTheme());

        // Update Social Links: Filter out any completely blank ones
        if (updatedData.getSocialLinks() != null) {
            List<SocialLink> validLinks = updatedData.getSocialLinks().stream()
                    .filter(link -> link.getTitle() != null && !link.getTitle().trim().isEmpty()
                            && link.getUrl() != null && !link.getUrl().trim().isEmpty())
                    .collect(java.util.stream.Collectors.toList());

            // Clear the old links and add the newly edited/filtered ones
            existingUser.getSocialLinks().clear();
            existingUser.getSocialLinks().addAll(validLinks);
        }

        return userRepository.save(existingUser);
    }

    // Can be updated to match themes found in the database
    public List<String> getAvailableThemes() {
        return java.util.Arrays.asList("default");
    }
}