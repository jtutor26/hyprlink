package com.basecamp.HyprLink.controller;

import com.basecamp.HyprLink.entity.User;
import com.basecamp.HyprLink.repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class TemplatesController {

    private final UserRepository userRepository;

    public TemplatesController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/templates")
    public String showTemplatesPage(Principal principal, Model model) {
        if (principal == null) {
            model.addAttribute("user", null);
        } else {
            User user = userRepository.findByUsername(principal.getName()).orElse(null);
            model.addAttribute("user", user);
        }

        return "templates";
    }
}