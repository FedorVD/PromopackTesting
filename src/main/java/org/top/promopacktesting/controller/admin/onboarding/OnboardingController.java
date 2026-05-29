package org.top.promopacktesting.controller.admin.onboarding;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.top.promopacktesting.service.UserService;

@Controller
@RequestMapping("/admin/onboarding")
@PreAuthorize("hasRole('ADMIN')")
public class OnboardingController {

    @Autowired
    private UserService userService;

    @GetMapping
    public String showOnboardingDashboard(Model model){
        model.addAttribute("username", userService.getCurrentUsername());
        System.out.println("Onboarding Dashboard");
        return "admin/onboarding/onboarding";
    }
}
