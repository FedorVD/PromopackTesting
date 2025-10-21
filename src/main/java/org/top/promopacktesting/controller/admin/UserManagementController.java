package org.top.promopacktesting.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.top.promopacktesting.model.User;
import org.top.promopacktesting.service.UserService;

import java.io.IOException;
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
                            @RequestParam(required = false) String position,
                            @RequestParam(required = false) String sortField,
                            @RequestParam(required = false) String sortDirection) {
        List<User> users;
        if (search != null || department != null || position != null) {
            users = userService.searchUsers(search, department, position);
        } else {
            users = userService.getAllActiveUsers();
        }

        if (sortField != null && !sortField.isEmpty()) {
            switch (sortField) {
                case "department":
                    if ("asc".equals(sortDirection)) {
                        users.sort((u1, u2) -> u1.getDepartment().compareTo(u2.getDepartment()));
                    } else if ("desc".equals(sortDirection)) {
                        users.sort((u1, u2) -> u2.getDepartment().compareTo(u1.getDepartment()));
                    }
                    break;
                case "position":
                    if ("asc".equals(sortDirection)) {
                        users.sort((u1, u2) -> u1.getPosition().compareTo(u2.getPosition()));
                    } else if ("desc".equals(sortDirection)) {
                        users.sort((u1, u2) -> u2.getPosition().compareTo(u1.getPosition()));
                    }
                    break;
            }
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
            return "admin/users/editUser";
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

    @GetMapping("/upload")
    public String showUploadForm(Model model) {
        return "admin/users/upload";
    }

    @PostMapping("/upload")
    public String uploadUsersFromExcel(@RequestParam("file") MultipartFile file, Model model) {
        System.out.println("Пользователь " + SecurityContextHolder.getContext().getAuthentication().getName() + " пытается загрузить файл" + file.getOriginalFilename());
        if (file.isEmpty()) {
            model.addAttribute("error", "Файл не выбран");
            return "admin/users/users";
        } else {
            try {
                List<User> uploadedUsers = userService.uploadUsersFromExcel(file.getInputStream());
                for (User user : uploadedUsers) {
                    Optional<User> existingUserOpt = userService.getUserByEmployeeId(user.getEmployeeId());
                    if (existingUserOpt.isPresent()) {
                        User existingUser = existingUserOpt.get();
                        existingUser.setUsername(user.getUsername());
                        existingUser.setName(user.getName());
                        existingUser.setDepartment(user.getDepartment());
                        existingUser.setPosition(user.getPosition());
                        existingUser.setHireDate(user.getHireDate());
                        existingUser.setUsername(existingUser.getUsername()); // Имя пользователя не меняем
                        existingUser.setPassword(existingUser.getPassword()); // Пароль не меняем
                        if (user.getDismissalDate() != null) {
                            existingUser.setDismissalDate(user.getDismissalDate());
                        }
                        userService.updateUser(existingUser);
                    }else{
                        userService.registerUser(user);
                    }
                }
                model.addAttribute("message", "Сотрудники успешно загружены и обновлены.");
                return "redirect:/admin/users/users";
            } catch (IOException e) {
                model.addAttribute("error", "Ошибка при загрузке файла: " + e.getMessage());
                return "error";
            } catch (Exception e) {
                model.addAttribute("error", "Ошибка при обработке файла: " + e.getMessage());
                return "error";
            }
        }
    }
}