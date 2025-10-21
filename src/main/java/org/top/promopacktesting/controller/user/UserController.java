package org.top.promopacktesting.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.top.promopacktesting.model.User;
import org.top.promopacktesting.service.UserService;

import java.util.Optional;

@Controller

@PreAuthorize("hasRole('USER')")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/user")
    public String userPanel(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();
        Optional<User> userOpt = userService.getUserByUsername(currentUsername);

        if (userOpt.isEmpty() || userOpt == null) {
            model.addAttribute("error", "Пользователь не найден");
            return "user/user";
        }
        User currentUser = userOpt.get();
        model.addAttribute("username", currentUser.getName());
        return "user/user";
    }
}