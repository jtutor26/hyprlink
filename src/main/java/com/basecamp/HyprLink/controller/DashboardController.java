package com.basecamp.HyprLink.controller;

import com.basecamp.HyprLink.entity.User;
import com.basecamp.HyprLink.service.DashboardService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;

@Controller
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/dashboard")
    public String showDashboard(Principal principal, Model model) {
        String username = principal.getName();
        User user = dashboardService.getUserForDashboard(username);

        model.addAttribute("user", user);
        model.addAttribute("themes", dashboardService.getAvailableThemes());
        return "dashboard";
    }

    @PostMapping("/dashboard/save")
    public String saveProfile(@ModelAttribute User updatedData, Principal principal) {
        dashboardService.updateUserProfile(updatedData, principal.getName());
        return "redirect:/dashboard?success";
    }
}