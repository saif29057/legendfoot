package com.ecommerce.app.security;

import com.ecommerce.app.entity.User;
import com.ecommerce.app.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {

    private final UserService userService;

    
    /**
     * Authentication manager bean.
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config
    ) throws Exception {

        return config.getAuthenticationManager();
    }

    /**
     * Custom UserDetailsService.
     */
    @Bean
    public org.springframework.security.core.userdetails.UserDetailsService userDetailsService() {

        return username -> {

            log.debug("Loading user: {}", username);

            User user = userService.getUserByUsername(username)
                    .orElseThrow(() ->
                            new UsernameNotFoundException(
                                    "User not found: " + username
                            )
                    );

            if (!user.isEnabled()) {
                throw new DisabledException(
                        "User account is disabled"
                );
            }

            return org.springframework.security.core.userdetails.User.builder()
                    .username(user.getUsername())
                    .password(user.getPassword())
                    .authorities(getAuthorities(user.getRole()))
                    .build();
        };
    }

    /**
     * Convert application roles to Spring Security authorities.
     */
    private GrantedAuthority[] getAuthorities(User.Role role) {

        if (role == User.Role.ADMIN) {

            return new GrantedAuthority[]{
                    new SimpleGrantedAuthority("ROLE_ADMIN")
            };
        }

        return new GrantedAuthority[]{
                new SimpleGrantedAuthority("ROLE_USER")
        };
    }

    /**
     * Main security configuration.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http

                // CSRF Configuration
                .csrf(csrf -> csrf
                        .csrfTokenRepository(
                                CookieCsrfTokenRepository.withHttpOnlyFalse()
                        )
                )

                // Authorization rules
                .authorizeHttpRequests(authorize -> authorize

                        // Public pages
                        .requestMatchers(
                                "/",
                                "/login",
                                "/register",
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/error/**",
                                "/favicon.ico"
                        ).permitAll()

                        // Admin pages
                        .requestMatchers("/admin/**")
                        .hasRole("ADMIN")

                        // Authenticated pages
                        .requestMatchers(
                                "/cart/**",
                                "/orders/**",
                                "/profile/**"
                        ).authenticated()

                        // Everything else
                        .anyRequest().authenticated()
                )

                // Login configuration
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/home", true)
                        .failureUrl("/login?error")
                        .usernameParameter("username")
                        .passwordParameter("password")
                        .permitAll()
                )

                // Logout configuration
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )

                // Exception handling
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(authenticationEntryPoint())
                        .accessDeniedPage("/access-denied")
                )

                // Security headers
                .headers(headers -> headers
                        .contentSecurityPolicy(csp ->
                                csp.policyDirectives("default-src 'self'")
                        )
                        .frameOptions(frame ->
                                frame.deny()
                        )
                );

        return http.build();
    }

    /**
     * Redirect unauthenticated users to login page.
     */
    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {

        return (request, response, authException) -> {

            log.debug(
                    "Unauthorized access to: {}",
                    request.getRequestURI()
            );

            if (!response.isCommitted()) {

                response.sendRedirect(
                        "/login?error=unauthorized"
                );
            }
        };
    }
}

