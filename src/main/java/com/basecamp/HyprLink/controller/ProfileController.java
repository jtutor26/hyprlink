package com.basecamp.HyprLink.controller;

import com.basecamp.HyprLink.entity.User;
import com.basecamp.HyprLink.service.ProfileService;
import java.security.Principal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping("/profile/me")
    public String getCurrentUserProfile(Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        User user = profileService.getUserProfileByUsername(principal.getName());
        if (user == null || user.getId() == null) {
            return "redirect:/dashboard";
        }

        return "redirect:/profile/" + user.getId();
    }

    @GetMapping("/profile/{id}")
    public String getProfileById(@PathVariable Long id, Model model) {
        User user = profileService.getUserProfileById(id);

        if (user == null) {
            return "error/404";
        }

        model.addAttribute("user", user);
        return "profile";
    }
}