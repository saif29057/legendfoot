package com.ecommerce.app.config;

import com.ecommerce.app.entity.User;
import com.ecommerce.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Ensures the application has a usable admin account on startup.
 *
 * The backend already supports ADMIN permissions, but the database does not
 * always contain an ADMIN user after local resets. This initializer creates a
 * default admin account when missing, or promotes the known account if it was
 * created with the wrong role.
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class AdminUserInitializer {

    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_EMAIL = "admin@ecommerce.com";
    private static final String ADMIN_PASSWORD = "admin123";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner seedAdminUser() {
        return args -> {
            userRepository.findByUsername(ADMIN_USERNAME)
                    .ifPresentOrElse(this::ensureAdminRole, this::createAdminUser);
        };
    }

    private void createAdminUser() {
        User admin = new User();
        admin.setUsername(ADMIN_USERNAME);
        admin.setEmail(ADMIN_EMAIL);
        admin.setPassword(passwordEncoder.encode(ADMIN_PASSWORD));
        admin.setRole(User.Role.ADMIN);
        admin.setEnabled(true);

        userRepository.save(admin);
        log.info("Created default admin user: {}", ADMIN_USERNAME);
    }

    private void ensureAdminRole(User user) {
        boolean updated = false;

        if (user.getRole() != User.Role.ADMIN) {
            user.setRole(User.Role.ADMIN);
            updated = true;
        }

        if (!user.isEnabled()) {
            user.setEnabled(true);
            updated = true;
        }

        if (user.getEmail() == null || !ADMIN_EMAIL.equalsIgnoreCase(user.getEmail())) {
            user.setEmail(ADMIN_EMAIL);
            updated = true;
        }

        if (updated) {
            user.setPassword(passwordEncoder.encode(ADMIN_PASSWORD));
            userRepository.save(user);
            log.info("Promoted existing user '{}' to ADMIN", ADMIN_USERNAME);
        }
    }
}