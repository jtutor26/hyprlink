package com.basecamp.HyprLink.controller;

import com.basecamp.HyprLink.entity.User;
import com.basecamp.HyprLink.service.ProfileService;
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

    @GetMapping("/profile/{id}")
    public String getProfile(@PathVariable Long id, Model model) {
        User user = profileService.getUserProfileById(id);

        if (user == null) {
            return "error/404";
        }

        model.addAttribute("user", user);
        return "profile";
    }
}