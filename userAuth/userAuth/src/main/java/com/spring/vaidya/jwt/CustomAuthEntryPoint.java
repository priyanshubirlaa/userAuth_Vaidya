package com.spring.vaidya.jwt;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Custom authentication entry point that handles unauthorized access attempts.
 * This class ensures that when an unauthenticated user tries to access a secured resource,
 * a structured JSON response is sent instead of a default error page.
 */
@Component
public class CustomAuthEntryPoint implements AuthenticationEntryPoint {

    /**
     * Handles unauthorized access by returning a structured JSON response.
     *
     * @param request       The HTTP request
     * @param response      The HTTP response
     * @param authException The authentication exception that was thrown
     * @throws IOException      If an input or output exception occurs
     * @throws ServletException If a servlet exception occurs
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        // Set response type as JSON
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 Unauthorized

        // Create a JSON error response
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", "Unauthorized - Invalid or missing token");
        errorResponse.put("status", HttpServletResponse.SC_UNAUTHORIZED);
        errorResponse.put("timestamp", System.currentTimeMillis());

        // Convert error response to JSON and write it to the response output
        response.getWriter().write(new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(errorResponse));
    }
}
