package com.basecamp.HyprLink.controller;

import com.basecamp.HyprLink.entity.User;
import com.basecamp.HyprLink.repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class LandingPageController {

    private final UserRepository userRepository;
    public LandingPageController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping()
    public String landingPage(Principal principal, Model model) {
        if (principal == null) {
            model.addAttribute("user", null);
            return "index";
        }
        User user = userRepository.findByUsername(principal.getName()).orElse(null);
        model.addAttribute("user", user);
        return "index";
    }
}
