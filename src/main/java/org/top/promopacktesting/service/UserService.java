package org.top.promopacktesting.service;

import jakarta.transaction.Transactional;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.top.promopacktesting.model.User;
import org.top.promopacktesting.repository.UserRepository;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final String defaultPassword = "12345678";

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .roles(String.valueOf(user.getRole()))
                .build();
    }

    public User registerUser(User user) {
        if (userRepository.existsByUsername(user.getUsername()) && user.getUsername() !=null) {
            throw new RuntimeException("Имя пользователя уже занято");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public void updateUser(User user) {
        userRepository.save(user);
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<User> getAllActiveUsers() {
        return userRepository.findByDismissalDateIsNull();
    }

    List<User> searchUsers(String keyword) {
        List<User> users = userRepository.findByNameContainingIgnoreCase(keyword);
        List<User> temp = userRepository.findByDepartmentContainingIgnoreCase(keyword);
        for (User user : temp) {
            if (!users.contains(user)) {
                users.add(user);
            }
        }
        temp = userRepository.findByDepartmentContainingIgnoreCase(keyword);
        for (User user : temp) {
            if (!users.contains(user)) {}
        }
        return users;
    }

    void resetPassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
    }

    public List<User>searchUsers(String name, String department, String position) {
        return userRepository.findByNameContainingIgnoreCaseAndDepartmentContainingIgnoreCaseAndPositionContainingIgnoreCase(name, department, position);
    }

    public List<User> searchUsersByDepartment(String department) {
        return userRepository.findByDepartmentContainingIgnoreCase(department);
    }

    public List<User> searchUsersByPosition(String position) {
        return userRepository.findByPositionContainingIgnoreCase(position);
    }

    public List<User> searchUsersByDepartmentAndPosition(String department, String position) {
        return userRepository.findByDepartmentContainingIgnoreCaseAndPositionContainingIgnoreCase(department, position);
    }

    public List<User> uploadUsersFromExcel(InputStream inputStream) throws IOException {
        List <User> users = new ArrayList<>();
        try (Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                if (row == null || isRowEmpty(row)) { continue; }
                    User user = new User();
                    user.setEmployeeId(row.getCell(0).getStringCellValue());
                    user.setName(row.getCell(1).getStringCellValue());
                    user.setDepartment(row.getCell(2).getStringCellValue());
                    user.setPosition(row.getCell(3).getStringCellValue());
                    user.setPassword(defaultPassword);
                    user.setRole(User.Role.USER);
                    if (row.getCell(4) != null) {
                        user.setHireDate(getDateFromExcel(row.getCell(4).getStringCellValue()));
                    }
                    if (row.getCell(5) != null) {
                        user.setHireDate(getDateFromExcel(row.getCell(5).getStringCellValue()));
                    }
                    users.add(user);

            }
        }
        return users;
    }

    private boolean isRowEmpty(Row row) {
        for (int i = row.getFirstCellNum(); i < row.getLastCellNum(); i++) {
            if (row.getCell(i) != null && !row.getCell(i).toString().trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    LocalDateTime getDateFromExcel(String dateStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        LocalDate localDate = LocalDate.parse(dateStr, formatter);
        LocalDateTime localDateTime = LocalDateTime.of(localDate, LocalTime.MIDNIGHT);
        return localDateTime;
    }

    public Optional<User> getUserByEmployeeId(String employeeId) {
        return userRepository.findByEmployeeId(employeeId);
    }
}
