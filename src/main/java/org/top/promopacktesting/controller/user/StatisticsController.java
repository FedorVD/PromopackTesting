package org.top.promopacktesting.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.top.promopacktesting.model.AssignedTest;
import org.top.promopacktesting.model.User;
import org.top.promopacktesting.service.AssignmentService;
import org.top.promopacktesting.service.UserService;

import java.util.List;
import java.util.Optional;

@Controller
public class StatisticsController {

    @Autowired
    private AssignmentService assignmentService;

    @Autowired
    private UserService userService;

    @GetMapping("/user/userStatistics")
    public String showUserStatistics(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUserName = auth.getName();

        Optional<User> userOpt = userService.getUserByUsername(currentUserName);
        if (userOpt.isEmpty()) {
            model.addAttribute("error", "Пользователь не найден");
            return "user/user";
        }

        User user = userOpt.get();
        List<AssignedTest> completedTests = assignmentService.getCompletedTestsByUserId(user);
        model.addAttribute("completedTests", completedTests);
        System.out.println("===> Запрос /user/statistics");
        return "user/userStatistics";
    }

    @ExceptionHandler
    public String handleException(Model model, Exception ex) {
        model.addAttribute("error", "Произошла ошибка" + ex.getMessage());
        return "user/user";
    }
}
