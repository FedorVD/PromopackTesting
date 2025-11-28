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
import org.springframework.web.servlet.view.RedirectView;
import org.top.promopacktesting.model.Department;
import org.top.promopacktesting.model.Position;
import org.top.promopacktesting.model.User;
import org.top.promopacktesting.repository.DepartmentRepository;
import org.top.promopacktesting.repository.PositionRepository;
import org.top.promopacktesting.service.DepartmentService;
import org.top.promopacktesting.service.PositionService;
import org.top.promopacktesting.service.UserService;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

@Controller
@RequestMapping("/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class UserManagementController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private PositionService positionService;

    @GetMapping("/users")
    public String showUsers(Model model,
                            @RequestParam(required = false) String search,
                            @RequestParam(required = false) Long departmentId,
                            @RequestParam(required = false) Long positionId,
                            @RequestParam(required = false) String sortField,
                            @RequestParam(required = false) String sortDirection) {
        sendCurrentUsername(model);
        List<User> users = new ArrayList<>();
        if (search != null && !search.isEmpty() && departmentId != null && positionId != null) {
            users = userService.searchUsers(search, departmentId, positionId);
        } else if (search != null && !search.isEmpty() && departmentId != null) {
            users = userService.searchUsersByNameAndDepartmentId(search, departmentId);
        }else if (search != null && !search.isEmpty() && positionId != null) {
            users = userService.searchUsersByNameAndPositionId(search, positionId);
        }else if (departmentId != null && positionId != null) {
            users = userService.searchUsersByDepartmentIdAndPositionId(departmentId, positionId);
        }else if (search != null && !search.isEmpty()) {
            users = userService.searchUsersByName(search);
        }else if (departmentId != null) {
            users = userService.searchUsersByDepartmentId(departmentId);
        }else if (positionId != null) {
            users = userService.searchUsersByPositionId(positionId);
        }else{
            users = userService.getAllActiveUsers();
        }

        if (sortField != null && !sortField.isEmpty()) {
            Comparator<User> comparator = switch (sortField) {
                case "department" -> Comparator.comparing(
                        u -> u.getDepartment() != null ? String.valueOf(u.getDepartment().getDepartmentName()) : "",
                        String.CASE_INSENSITIVE_ORDER
                );
                case "position" -> Comparator.comparing(
                        u -> u.getPosition() != null ? String.valueOf(u.getPosition().getPositionName()) : "",
                        String.CASE_INSENSITIVE_ORDER);
                default -> Comparator.comparing(User::getUsername);
            };
            if ("desc".equals(sortDirection)) {
                comparator = comparator.reversed();
            }
            users.sort(comparator);
        }


        model.addAttribute("users", users);
        model.addAttribute("search", search);
        model.addAttribute("departments", departmentService.findAllDepartments());
        model.addAttribute("positions", positionService.findAllPositions());
        model.addAttribute("departmentId", departmentId);
        model.addAttribute("positionId", positionId);
        return "admin/users/users";
    }

    @GetMapping("/addUser")
    public String showAddUserForm(Model model) {
        sendCurrentUsername(model);
        model.addAttribute("roles", Arrays.asList(User.Role.values()));
        model.addAttribute("departments", departmentService.findAllDepartments());
        model.addAttribute("positions", positionService.findAllPositions());
        return "admin/users/addUser";
    }

    @PostMapping("/addUser")
    public String addUser(@RequestParam String username,
                          @RequestParam String password,
                          @RequestParam User.Role role,
                          @RequestParam String employeeId,
                          @RequestParam String name,
                          @RequestParam Long departmentId,
                          @RequestParam Long positionId,
                          @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hireDate,
                          Model model) {
        try {
            Department department = departmentService.findByDepartmentId(departmentId).orElse(null);
            Position position = positionService.findByPositionId(positionId).orElse(null);
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

    @GetMapping("/editUser")
    public String showEditUserForm(@RequestParam Long id,
                                   @RequestParam(required = false) String search,
                                   Model model) {
        sendCurrentUsername(model);
        Optional<User> userOpt = userService.getUserById(id);
        if (userOpt.isEmpty()) {
            model.addAttribute("error", "Пользователь не найден");
            return "admin/users/editUser";
        }
        User user = userOpt.get();
        model.addAttribute("search", search);
        model.addAttribute("departments", departmentService.findAllDepartments());
        model.addAttribute("positions", positionService.findAllPositions());
        model.addAttribute("departmentId", user.getDepartment().getId());
        model.addAttribute("positionId", user.getPosition().getId());
        model.addAttribute("user", user);
        model.addAttribute("roles", Arrays.asList(User.Role.values()));
        return "admin/users/editUser";
    }

    @PostMapping("/editUser")
    public RedirectView editUser(@RequestParam Long id,
                           @RequestParam String username,
                           @RequestParam(required = false) String password,
                           @RequestParam User.Role role,
                           @RequestParam String employeeId,
                           @RequestParam String name,
                           @RequestParam Long departmentEditId,
                           @RequestParam Long positionEditId,
                           @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hireDate,
                           @RequestParam(required = false) String search,
                           @RequestParam(required = false) Long departmentId,
                           @RequestParam(required = false) Long positionId,
                           Model model) {

        //User user;
        try {
            Department departmentEdit = departmentService.findByDepartmentId(departmentEditId).orElse(null);
            Position positionEdit = positionService.findByPositionId(positionEditId).orElse(null);
/*
            Department department = departmentService.findByDepartmentId(departmentId).orElse(null);
            Position position = positionService.findByPositionId(positionId).orElse(null);
*/
            Optional<User> userOpt = userService.getUserById(id);
            if (userOpt.isEmpty()) {
                model.addAttribute("error", "Пользователь не найден.");
                return new RedirectView("admin/users/editUser");
            }

            User user = userOpt.get();
            user.setUsername(username);

            if (password != null && !password.isEmpty()) {
                String encodedPassword = passwordEncoder.encode(password);
                user.setPassword(encodedPassword);
            }
            user.setRole(role);
            user.setEmployeeId(employeeId);
            user.setName(name);
            user.setDepartment(departmentEdit);
            user.setPosition(positionEdit);
            user.setHireDate(hireDate);

            userService.updateUser(user);
            model.addAttribute("message", "Пользователь успешно обновлён!");
            StringBuilder redirectUrl = new StringBuilder("/admin/users/users");
            List<String> params = new ArrayList<>();
            if (search != null && !search.isEmpty()) {
                params.add("search=" + URLEncoder.encode(search, StandardCharsets.UTF_8));
            }
            if (departmentId != null) {
                params.add("departmentId=" + departmentId);
            }
            if (positionId != null) {
                params.add("positionId=" + positionId);
            }

            // Если есть параметры, добавляем ? перед первым
            if (!params.isEmpty()) {
                redirectUrl.append("?").append(String.join("&", params));
            }

            return new RedirectView(redirectUrl.toString());
        } catch (Exception e) {
            model.addAttribute("error", "Ошибка при обновлении пользователя: " + e.getMessage());
        }

        model.addAttribute("user", userService.getUserById(id).orElse(null));
        return new RedirectView("admin/users/editUser");
    }

    @GetMapping("/upload")
    public String showUploadForm(Model model) {
        sendCurrentUsername(model);
        model.addAttribute("departments", departmentService.findAllDepartments());
        model.addAttribute("positions", positionService.findAllPositions());
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
                    if (existingUserOpt.isPresent()) { // Если пользователь уже существует, обновляем его данные
                        User existingUser = existingUserOpt.get();
                        //existingUser.setUsername(user.getUsername());
                        existingUser.setName(user.getName());
                        existingUser.setDepartment(user.getDepartment());
                        existingUser.setPosition(user.getPosition());
                        existingUser.setHireDate(user.getHireDate());
                        existingUser.setPassword(existingUser.getPassword()); // Пароль не меняем
                        existingUser.setUsername(existingUser.getUsername()); // Имя пользователя не меняем
                        if (user.getDismissalDate() != null) {
                            existingUser.setDismissalDate(user.getDismissalDate());
                        }
                        userService.updateUser(existingUser);
                    }else{ // Если пользователя не существует, добавляем его
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

    public void sendCurrentUsername(Model model) {
        if (Objects.equals(userService.getCurrentUsername(), "Пользователь не найден")){
            model.addAttribute("error", "Пользователь не найден");
            model.addAttribute("username", "Ошибка входа");
        } else {
            model.addAttribute("username", userService.getCurrentUsername());
        }
    }
}