package org.top.promopacktesting.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.top.promopacktesting.model.User;
import org.top.promopacktesting.service.UserService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class UserManagementController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/users")
    public String showUsers(Model model,
                            @RequestParam(required = false) String search,
                            @RequestParam(required = false) String department,
                            @RequestParam(required = false) String position) {
        List<User> users;
        if (search != null || department != null || position != null) {
            users = userService.searchUsers(search, department, position);
        }else {
            users = userService.getAllActiveUsers();
        }
        model.addAttribute("users", users);
        model.addAttribute("search", search);
        model.addAttribute("department", department);
        model.addAttribute("position", position);
        return "admin/users/users";
    }

    @GetMapping("/addUser")
    public String showAddUserForm(Model model) {
        model.addAttribute("roles", Arrays.asList(User.Role.values()));
        return "admin/users/addUser";
    }

    @PostMapping("/addUser")
    public String addUser(@RequestParam String username,
                          @RequestParam String password,
                          @RequestParam User.Role role,
                          @RequestParam String employeeId,
                          @RequestParam String name,
                          @RequestParam String department,
                          @RequestParam String position,
                          @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hireDate,
                          Model model) {
        try {
            String encodedPassword = passwordEncoder.encode(password);
            User newUser = new User(username, encodedPassword, role,
                    employeeId, name, department, position, hireDate);
            userService.registerUser(newUser);
            model.addAttribute("message", "Пользователь успешно добавлен!");
            return "redirect:/admin/users/users";
        } catch (Exception e) {
            model.addAttribute("error", "Ошибка при добавлении пользователя: " + e.getMessage());
        }
        return "admin/users/addUser";
    }

    @GetMapping("/{id}/editUser")
    public String showEditUserForm(@PathVariable Long id, Model model) {
        Optional<User> userOpt = userService.getUserById(id);
        if (userOpt.isEmpty()) {
            model.addAttribute("error", "Пользователь не найден");
            return  "admin/users/editUser";
        }
        User user = userOpt.get();
        model.addAttribute("user", user);
        model.addAttribute("roles", Arrays.asList(User.Role.values()));
        return "admin/users/editUser";
    }

    @PostMapping("/{id}/editUser")
    public String editUser(@PathVariable Long id,
                           @RequestParam String username,
                           @RequestParam(required = false) String password,
                           @RequestParam User.Role role,
                           @RequestParam String employeeId,
                           @RequestParam String name,
                           @RequestParam String department,
                           @RequestParam String position,
                           @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hireDate,
                           Model model) {

        User user;
        try {
            Optional<User> userOpt = userService.getUserById(id);
            if (userOpt.isEmpty()) {
                model.addAttribute("error", "Пользователь не найден.");
                return "admin/users/editUser";
            }

            user = userOpt.get();
            user.setUsername(username);

            if (password != null && !password.isEmpty()) {
                String encodedPassword = passwordEncoder.encode(password);
                user.setPassword(encodedPassword);
            }
            user.setRole(role);
            user.setEmployeeId(employeeId);
            user.setName(name);
            user.setDepartment(department);
            user.setPosition(position);
            user.setHireDate(hireDate);

            userService.updateUser(user);
            model.addAttribute("message", "Пользователь успешно обновлён!");
            return "redirect:/admin/users/users";
        } catch (Exception e) {
            model.addAttribute("error", "Ошибка при обновлении пользователя: " + e.getMessage());
        }

        model.addAttribute("user", userService.getUserById(id).orElse(null));
        return "admin/users/editUser";
    }
}
