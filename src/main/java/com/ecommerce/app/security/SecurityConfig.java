package com.ecommerce.app.security;

import com.ecommerce.app.entity.User;
import com.ecommerce.app.security.jwt.JwtAuthenticationFilter;
import com.ecommerce.app.security.jwt.JwtAuthenticationSuccessHandler;
import com.ecommerce.app.security.jwt.JwtLogoutSuccessHandler;
import com.ecommerce.app.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

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

            User user = userService
                .getUserByUsername(username)
                .or(() -> userService.getUserByEmail(username))
                .orElseThrow(() ->
                    new UsernameNotFoundException("User not found: " + username)
                );

            if (!user.isEnabled()) {
                throw new DisabledException("User account is disabled");
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
            return new GrantedAuthority[] {
                new SimpleGrantedAuthority("ROLE_ADMIN"),
            };
        }

        return new GrantedAuthority[] {
            new SimpleGrantedAuthority("ROLE_USER"),
        };
    }

    /**
     * Main security configuration.
     */
    @Bean
    public SecurityFilterChain filterChain(
        HttpSecurity http,
        JwtAuthenticationFilter jwtAuthenticationFilter,
        JwtAuthenticationSuccessHandler jwtAuthenticationSuccessHandler,
        JwtLogoutSuccessHandler jwtLogoutSuccessHandler
    ) throws Exception {
        http

            // Stateless JWT authentication does not use server-side CSRF tokens.
            .csrf(AbstractHttpConfigurer::disable)

            // Keep authentication stateless so each request is resolved from the JWT
            // cookie.
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            // Authorization rules
            // Note: Order matters! More specific rules must come before general ones.
            .authorizeHttpRequests(authorize ->
                authorize

                    // Public pages
                    .requestMatchers(
                        "/",
                        "/home",
                        "/login",
                        "/register",
                        "/users/register",
                        "/about",
                        "/contact",
                        "/access-denied",
                        "/css/**",
                        "/js/**",
                        "/images/**",
                        "/error/**",
                        "/favicon.ico"
                    )
                    .permitAll()

                    // Admin pages (specific routes before general /products/**)
                    .requestMatchers(
                        "/admin/**",
                        "/products/new",
                        "/products/*/edit",
                        "/products/*/delete",
                        "/orders/admin/**"
                    )
                    .hasRole("ADMIN")

                    // Authenticated pages
                    .requestMatchers(
                        "/cart/**",
                        "/orders/**",
                        "/users/profile/**"
                    )
                    .authenticated()

                    // Product browsing (general route that matches everything under /products)
                    // This comes AFTER specific admin routes so admin checks apply first
                    .requestMatchers("/products/**")
                    .permitAll()

                    // Everything else
                    .anyRequest()
                    .authenticated()
            )

            // Login configuration
            .formLogin(form ->
                form
                    .loginPage("/login")
                    .loginProcessingUrl("/login")
                    .successHandler(jwtAuthenticationSuccessHandler)
                    .failureUrl("/login?error")
                    .usernameParameter("username")
                    .passwordParameter("password")
                    .permitAll()
            )

            // Logout configuration
            .logout(logout ->
                logout
                    .logoutUrl("/logout")
                    .logoutSuccessHandler(jwtLogoutSuccessHandler)
                    .invalidateHttpSession(false)
                    .permitAll()
            )

            // JWT filter resolves the authenticated user for every request.
            .addFilterBefore(
                jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class
            )

            // Exception handling
            .exceptionHandling(exception ->
                exception
                    .authenticationEntryPoint(authenticationEntryPoint())
                    .accessDeniedPage("/access-denied")
            )

            // Security headers
            .headers(headers ->
                headers
                    .contentSecurityPolicy(csp ->
                        csp.policyDirectives(
                            "default-src 'self'; " +
                                "base-uri 'self'; " +
                                "object-src 'none'; " +
                                "frame-ancestors 'self'; " +
                                "style-src 'self' 'unsafe-inline' https://cdn.jsdelivr.net https://cdnjs.cloudflare.com; " +
                                "script-src 'self' 'unsafe-inline' https://cdn.jsdelivr.net; " +
                                "connect-src 'self' https://cdn.jsdelivr.net; " +
                                "img-src 'self' data:; " +
                                "font-src 'self' https://cdnjs.cloudflare.com"
                        )
                    )
                    .frameOptions(frame -> frame.deny())
            );

        return http.build();
    }

    /**
     * Redirect unauthenticated users to login page.
     */
    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, authException) -> {
            log.debug("Unauthorized access to: {}", request.getRequestURI());

            if (!response.isCommitted()) {
                response.sendRedirect(
                    request.getContextPath() + "/login?error=unauthorized"
                );
            }
        };
    }
}
