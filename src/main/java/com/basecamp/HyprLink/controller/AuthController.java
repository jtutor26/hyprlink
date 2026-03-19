package com.basecamp.HyprLink.controller;

import com.basecamp.HyprLink.entity.User;
import com.basecamp.HyprLink.service.AuthService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
@Controller
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", authService.prepareRegistrationFormData());
        model.addAttribute("themes", authService.getAvailableThemes());
        return "auth/register";
    }

    @PostMapping("/register/check")
    public String checkRegistrationForm(@ModelAttribute User user, Model model) {
        boolean validUser = true;
        model.addAttribute("themes", List.of("default", "dark"));
        if (!authService.checkUsername(user.getUsername())) {
            model.addAttribute("invalidUsername", true);
            validUser = false;
            System.out.print("Invalid username");
        }
        if (authService.checkUserDoesNotExist(user.getUsername())) {
            model.addAttribute("userAlreadyExists", true);
            if (validUser) {
                validUser = false;
            }
            System.out.print("Username exists alredy");
        }
        if (!authService.checkPassword(user.getPassword())) {
            model.addAttribute("invalidPassword", true);
            if (validUser) {
                validUser = false;
            }
        }
        if (validUser) {
            return registerUser(user);
        }
        return "auth/register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user) {
        authService.registerUser(user);
        return "redirect:/login?success";
    }
}