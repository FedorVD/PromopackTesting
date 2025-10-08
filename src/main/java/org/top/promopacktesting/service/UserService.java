package org.top.promopacktesting.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.top.promopacktesting.model.User;
import org.top.promopacktesting.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

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
}
