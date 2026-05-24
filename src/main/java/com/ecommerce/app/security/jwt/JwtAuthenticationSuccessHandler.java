package com.ecommerce.app.security.jwt;

import java.io.IOException;
import java.time.Duration;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

/**
 * Issues a JWT cookie after a successful login.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtService jwtService;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        String token = jwtService.generateToken(authentication);
        ResponseCookie cookie = ResponseCookie.from(jwtService.getCookieName(), token)
                .httpOnly(true)
                .secure(request.isSecure())
                .path(resolveCookiePath(request))
                .sameSite("Lax")
                .maxAge(Duration.ofMillis(jwtService.getExpirationMs()))
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        response.sendRedirect(resolveRedirectPath(request, "/home"));
    }

    private String resolveCookiePath(HttpServletRequest request) {
        String contextPath = request.getContextPath();
        return (contextPath == null || contextPath.isBlank()) ? "/" : contextPath;
    }

    private String resolveRedirectPath(HttpServletRequest request, String targetPath) {
        String contextPath = request.getContextPath();
        return (contextPath == null ? "" : contextPath) + targetPath;
    }
}