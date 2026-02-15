package com.example.knightWatch.config;

import com.example.knightWatch.model.User;
import com.example.knightWatch.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Set;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {

            if (userRepository.count() == 0) {

                User user = new User();
                user.setUsername("user");
                user.setEmail("user@email.com");
                user.setPassword(passwordEncoder.encode("password"));
                user.setRoles(Set.of("USER"));

                User user1 = new User();
                user1.setUsername("user1");
                user1.setEmail("user1@email.com");
                user1.setPassword(passwordEncoder.encode("password1"));
                user1.setRoles(Set.of("USER"));

                User admin = new User();
                admin.setUsername("admin");
                admin.setEmail("admin@email.com");
                admin.setPassword(passwordEncoder.encode("adminpass"));
                admin.setRoles(Set.of("ADMIN", "USER"));

                userRepository.saveAll(List.of(user, user1, admin));

                System.out.println("Default users created: admin, user and user1");
            }
        };
    }
}

