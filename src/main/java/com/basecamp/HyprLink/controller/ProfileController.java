package com.basecamp.HyprLink.controller;

import com.basecamp.HyprLink.entity.User;
import com.basecamp.HyprLink.repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ProfileController {

    private final UserRepository userRepository;

    // Injecting the repository via the constructor
    public ProfileController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // The {id} is the dynamic part of the URL
    @GetMapping("/profile/{id}")
    public String getProfile(@PathVariable Long id, Model model) {

        // 1. Ask the database for the user matching the ID in the URL
        User user = userRepository.findById(id).orElse(null);

        // 2. If the user doesn't exist, you could route them to a 404 page
        if (user == null) {
            return "error/404";
        }

        // 3. Attach the found user object to the Thymeleaf model
        // The string "user" must perfectly match the ${user.something} variables in your HTML
        model.addAttribute("user", user);

        // 4. Return the exact name of your Thymeleaf HTML file (without the .html extension)
        return "profile";
    }
}