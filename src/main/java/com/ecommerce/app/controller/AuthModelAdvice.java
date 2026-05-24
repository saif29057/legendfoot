package com.ecommerce.app.controller;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.ControllerAdvice;

import java.util.Collection;

/**
 * Exposes authentication state to all MVC templates.
 *
 * The home page and shared header now read explicit model attributes instead
 * of relying only on Thymeleaf security tags. This keeps the navigation
 * deterministic and makes the authenticated state easy to reason about.
 */
@ControllerAdvice(annotations = Controller.class)
public class AuthModelAdvice {

    @ModelAttribute("authenticated")
    public boolean authenticated(Authentication authentication) {
        return isAuthenticated(authentication);
    }

    @ModelAttribute("currentUsername")
    public String currentUsername(Authentication authentication) {
        if (!isAuthenticated(authentication)) {
            return null;
        }

        return authentication.getName();
    }

    @ModelAttribute("currentRole")
    public String currentRole(Authentication authentication) {
        if (!isAuthenticated(authentication)) {
            return null;
        }

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        if (authorities == null) {
            return null;
        }

        return authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse(null);
    }

    @ModelAttribute("isAdmin")
    public boolean isAdmin(Authentication authentication) {
        String role = currentRole(authentication);
        return "ROLE_ADMIN".equals(role);
    }

    private boolean isAuthenticated(Authentication authentication) {
        return authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken);
    }
}