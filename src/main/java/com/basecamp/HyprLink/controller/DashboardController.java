package com.basecamp.HyprLink.controller;

import com.basecamp.HyprLink.entity.SocialLink;
import com.basecamp.HyprLink.entity.User;
import com.basecamp.HyprLink.repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;
import java.util.List;

@Controller
public class DashboardController {

    private final UserRepository userRepository;

    public DashboardController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/dashboard")
    public String showDashboard(Principal principal, Model model) {
        String username = principal.getName();
        User user = userRepository.findByUsername(username).orElse(null);

        // Add a blank link slot at the end so the user always has room to add a new one
        if (user != null) {
            user.getSocialLinks().add(new SocialLink());
        }

        model.addAttribute("user", user);
        model.addAttribute("themes", List.of("default", "dark"));
        return "dashboard";
    }

    @PostMapping("/dashboard/save")
    public String saveProfile(@ModelAttribute User updatedData, Principal principal) {
        User existingUser = userRepository.findByUsername(principal.getName()).orElseThrow();

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
                    .filter(link -> link.getTitle() != null && !link.getTitle().isBlank()
                            && link.getUrl() != null && !link.getUrl().isBlank())
                    .toList();

            // Clear the old links and add the newly edited/filtered ones
            existingUser.getSocialLinks().clear();
            existingUser.getSocialLinks().addAll(validLinks);
        }

        userRepository.save(existingUser);
        return "redirect:/dashboard?success";
    }
}