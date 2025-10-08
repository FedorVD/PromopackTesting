package org.top.promopacktesting;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.top.promopacktesting.model.User;
import org.top.promopacktesting.repository.UserRepository;

import static org.top.promopacktesting.model.User.Role.*;

@SpringBootApplication
public class PromopackTestingApplication {

    public static void main(String[] args) {
        SpringApplication.run(PromopackTestingApplication.class, args);
    }


    @Bean
    public CommandLineRunner commandLineRunner(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.findByRole(ADMIN).isEmpty()) {
                User admin=new User();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin"));
                admin.setRole(ADMIN);
                userRepository.save(admin);
            }

            if (userRepository.findByRole(USER).isEmpty()) {
                User user=new User();
                user.setUsername("user");
                user.setPassword(passwordEncoder.encode("user"));
                user.setRole(USER);
                userRepository.save(user);
            }
        };
    }
}
