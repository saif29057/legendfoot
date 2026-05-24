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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

/**
 * Clears the JWT cookie on logout.
 */
@Component
@RequiredArgsConstructor
public class JwtLogoutSuccessHandler implements LogoutSuccessHandler {

    private final JwtService jwtService;

    @Override
    public void onLogoutSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        ResponseCookie cookie = ResponseCookie.from(jwtService.getCookieName(), "")
                .httpOnly(true)
                .secure(request.isSecure())
                .path(resolveCookiePath(request))
                .sameSite("Lax")
                .maxAge(Duration.ZERO)
                .build();

        SecurityContextHolder.clearContext();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        response.sendRedirect(resolveRedirectPath(request, "/login?logout"));
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