package com.basecamp.HyprLink.controller;

import com.basecamp.HyprLink.entity.User;
import com.basecamp.HyprLink.service.ProfileService;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.Optional;

@Controller
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping("/profile/id/{id}")
    public String getProfileById(@PathVariable Long id, Model model) {
        User user = profileService.getUserProfileById(id);

        if (user == null) {
            return "error/404";
        }

        model.addAttribute("user", user);
        return "profile";
    }
    @PostMapping("/profile/find") public String searchProfile(@RequestParam String userName, Principal principal, Model model) {
        return processUserInfoByUsername(userName, principal, model);
    }
    @GetMapping("/profile/username/{userName}")
    public String getProfileByUsername(@PathVariable String userName, Principal principal, Model model) {
        return processUserInfoByUsername(userName, principal, model);
    }

    // Helper Methods \\
    @NonNull
    private String processUserInfoByUsername(String userName, Principal principal, Model model) {
        User user = profileService.getUserProfileByUsername(userName);
        return settingMethodAttributes(principal, model, user);
    }
    @NonNull
    private String processUserInfoById(Long id, Principal principal, Model model) {
        User user = profileService.getUserProfileById(id);
        return settingMethodAttributes(principal, model, user);
    }

    @NonNull
    private String settingMethodAttributes(Principal principal, Model model, User user) {
        if (user == null) {
            return "index";
        }
        if (principal == null) {
            model.addAttribute("signedIn", false);
            model.addAttribute("principalName", null);
        } else if (Optional.ofNullable(profileService.getUserProfileByUsername(principal.getName())).isPresent()) {
            model.addAttribute("signedIn", true);
            model.addAttribute("principalName", principal.getName());
        }
        if (principal != null && user.getUsername().equals(principal.getName())) {
            model.addAttribute("usersProfile", true);
        } else {
            model.addAttribute("usersProfile", false);
        }
        model.addAttribute("user", user);
        return "profile";
    }
}