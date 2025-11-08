package com.recognition.config;

import com.recognition.entity.Users;
import com.recognition.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.OffsetDateTime;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initAdmin(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.findByRole("admin").isEmpty()) {
                Users admin = new Users();
                admin.setUsername("admin");
                admin.setEmail("admin@system.local");
                admin.setPasswordHash(passwordEncoder.encode("Admin@123"));
                admin.setRole("admin");
                admin.setIsActive(true);
                admin.setIsVerified(true);
                admin.setCreatedAt(OffsetDateTime.now());
                admin.setUpdatedAt(OffsetDateTime.now());

                userRepository.save(admin);
                System.out.println("✅ Default admin created: username=admin, password=Admin@123");
            } else {
                System.out.println("ℹ️ Admin already exists, skipping...");
            }
        };
    }
}
