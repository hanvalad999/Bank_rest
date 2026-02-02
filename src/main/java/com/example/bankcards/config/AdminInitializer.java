package com.example.bankcards.config;

import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AdminProperties adminProperties;

    public AdminInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder, AdminProperties adminProperties) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminProperties = adminProperties;
    }

    @Override
    public void run(String... args) {
        if (adminProperties.getUsername() == null || adminProperties.getPassword() == null) {
            return;
        }
        if (userRepository.existsByUsername(adminProperties.getUsername())) {
            return;
        }
        User admin = User.builder()
                .username(adminProperties.getUsername())
                .password(passwordEncoder.encode(adminProperties.getPassword()))
                .role(Role.ADMIN)
                .build();
        userRepository.save(admin);
    }
}