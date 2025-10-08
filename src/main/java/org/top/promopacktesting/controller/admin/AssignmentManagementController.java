package org.top.promopacktesting.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.top.promopacktesting.model.AssignedTest;
import org.top.promopacktesting.model.Test;
import org.top.promopacktesting.model.User;
import org.top.promopacktesting.service.AssignmentService;
import org.top.promopacktesting.service.TestService;
import org.top.promopacktesting.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping("/admin/assignments")
@PreAuthorize("hasRole('ADMIN')")
public class AssignmentManagementController {

    @Autowired
    private AssignmentService assignmentService;

    @Autowired
    private UserService userService;

    @Autowired
    private TestService testService;

    @GetMapping("/assignTest")
    public String showAssignTestForm(Model model) {
        model.addAttribute("users", userService.getAllActiveUsers());
        model.addAttribute("tests", testService.getAllActiveTests());
        model.addAttribute("assignments",assignmentService.getAssignedTests());
        return "admin/assignments/assignTest";
    }

    @PostMapping("/assignTest")
    public String assignTestToUser(@RequestParam Long testId,
                                   @RequestParam Long userId,
                                   Model model) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String currentUsername = auth.getName();
            User assigner = userService.getUserByUsername(currentUsername).get();
            assignmentService.assignTestToUser(testId, userId, assigner);
            List<User> users = userService.getAllActiveUsers();
            List<Test> tests = testService.getAllActiveTests();
            model.addAttribute("message", "Тест успешно назначен пользователю: " + userService.getUserById(userId).get().getName());
        } catch (Exception e) {
            model.addAttribute("error", "Ошибка при назначении теста: " + e.getMessage());
        }
        return "redirect:/admin/assignments/assignments";
    }

    @GetMapping("/{assignmentId}/assignedTest")
    public String showAssignedTestForm(@PathVariable Long assignmentId, Model model) {
        AssignedTest assignedTest = assignmentService.getAssignedTestById(assignmentId).orElseThrow(() -> new RuntimeException("Тест не найден"));
        model.addAttribute("assignment",assignedTest);
        return "admin/assignments/assignedTest";
    }

    @GetMapping("/assignments")
    public String getAssignments(@RequestParam (required = false) Long testId,
                                    @RequestParam (required = false) Long userId,
                                    @RequestParam (required = false) AssignedTest.TestStatus status,
                                    Model model) {
        List<AssignedTest> assignments = new ArrayList<>();
        AssignedTest assignedTest;
        if (testId != null && userId != null && status != null) {
            assignments = assignmentService.getAssignedTestsByUserIdAndTestIdAndStatus(userId, testId, status);
        }else if(testId != null && userId != null){
            assignedTest = assignmentService.getAssignedTestByUserIdAndTestId(userId, testId).get();
            assignments.add(assignedTest);
        }else if (userId != null && status != null){
            assignments = assignmentService.getAssignedTestsByUserIdAndStatus(userId, status);
        }else if (testId != null && status != null){
            assignments = assignmentService.getAssignedTestsByTestIdAndStatus(testId, status);
        }else if (userId != null){
            assignments = assignmentService.getAssignedTestsByUserId(userId);
        }else if (testId != null){
            assignments = assignmentService.getAssignedTestsByTestId(testId);
        }else if (status != null){
            assignments = assignmentService.getAssignedTestsByStatus(status);
        }else {
            assignments = assignmentService.getAssignedTests();
        }
        if (assignments.isEmpty()) {
            model.addAttribute("message", "По выбранным параметрам поиска тесты не найдены");
        } else {
            model.addAttribute("assignments", assignments);
        }
        return "admin/assignments/assignments";
    }
}
