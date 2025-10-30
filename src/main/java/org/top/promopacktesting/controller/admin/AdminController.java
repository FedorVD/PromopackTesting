package org.top.promopacktesting.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.top.promopacktesting.model.User;
import org.top.promopacktesting.service.UserService;

import java.util.Objects;
import java.util.Optional;

@Controller
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private UserService userService;

    @GetMapping("/admin")
    public String adminPanel(Model model){
/*        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();
        Optional<User> userOpt = userService.getUserByUsername(currentUsername);

        if (userOpt.isEmpty()) {
            model.addAttribute("error", "Пользователь не найден");
            return "/admin/admnin";
        }
        User currentUser = userOpt.get();*/
        if (Objects.equals(userService.getCurrentUsername(), "Пользователь не найден")){
            model.addAttribute("error", "Пользователь не найден");
            model.addAttribute("username", "Ошибка входа");
        } else {
            model.addAttribute("username", userService.getCurrentUsername());
        }
        return "admin/admin";
    }
}
