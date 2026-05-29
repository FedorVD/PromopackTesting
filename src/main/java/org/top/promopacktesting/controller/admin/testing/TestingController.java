package org.top.promopacktesting.controller.admin.testing;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.top.promopacktesting.service.UserService;
import org.top.promopacktesting.service.test.AssignmentService;
import org.top.promopacktesting.service.test.TestService;
import org.top.promopacktesting.service.test.ThemeTestService;

@Controller
@RequestMapping("/admin/testing")
@PreAuthorize("hasRole('ADMIN')")
public class TestingController {

    @Autowired
    private ThemeTestService themeTestService;

    @Autowired
    private TestService testService;

    @Autowired
    private AssignmentService assignmentService;

    @Autowired
    private UserService userService;

    @GetMapping
    public String showTestingDashboard(Model model){
        model.addAttribute("username", userService.getCurrentUsername());
        model.addAttribute("themeTests", themeTestService.getAllThemeTests());
        model.addAttribute("tests", testService.getAllTests());
        model.addAttribute("assignments", assignmentService.getAssignedTests());
        return "admin/testing/testing";
    }
}
