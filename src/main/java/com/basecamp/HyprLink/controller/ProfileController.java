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

    @GetMapping("/profile")
    public String searchProfile(Principal principal, Model model) {
        if (principal == null) {
            return "index";
        }
        return processUserInfoByUsername(principal.getName(), principal, model);
    }

    @GetMapping("/profile/{userName}")
    public String getProfileByUsername(@PathVariable String userName, Principal principal, Model model) {
        return processUserInfoByUsername(userName, principal, model);
    }

    // Helper Methods
    private String processUserInfoByUsername(String userName, Principal principal, Model model) {
        User user = profileService.getUserProfileByUsername(userName);
        return settingMethodAttributes(principal, model, user);
    }

    private String processUserInfoById(Long id, Principal principal, Model model) {
        User user = profileService.getUserProfileById(id);
        return settingMethodAttributes(principal, model, user);
    }

    private String settingMethodAttributes(Principal principal, Model model, User user) {
        if (user == null) {
            return "index";
        }
        if (principal == null) {
            model.addAttribute("signedIn", false);
            model.addAttribute("principalName", null);
        } else if (profileService.getUserProfileByUsername(principal.getName()) != null) {
            model.addAttribute("signedIn", true);
            model.addAttribute("principalName", principal.getName());
        }
        if (principal != null && user.getUsername() != null && user.getUsername().equals(principal.getName())) {
            model.addAttribute("usersProfile", true);
        } else {
            model.addAttribute("usersProfile", false);
        }
        model.addAttribute("user", user);
        return "profile";
    }

}