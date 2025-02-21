package com.spring.vaidya.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.spring.vaidya.jwt.JwtAuthFilter;

/**
 * Security configuration class for Spring Security setup.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthFilter authFilter;
    private final UserDetailsService userDetailsService;

    /**
     * Constructor-based dependency injection of JWT Authentication Filter and UserDetailsService.
     */
    public SecurityConfig(JwtAuthFilter authFilter, UserDetailsService userDetailsService) {
        this.authFilter = authFilter;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Configures HTTP security, specifying authorization rules and security filters.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                // Disable CSRF protection (useful for stateless APIs)
                .csrf(csrf -> csrf.disable())
                
                // Configure authorization rules
                .authorizeHttpRequests(authorize -> authorize
                        // Public endpoints that do not require authentication
                        .requestMatchers("/auth/*", "/user/new", "/doctor/confirm-account", "doctor/register", 
                                         "/user/login", "/user/authenticate", "/user/welcome", "login/doctor").permitAll()
                        
                        // Protected endpoints that require authentication
                        .requestMatchers("/user/protected", "doctor/all").authenticated()
                )
                
                // Configure session management as stateless (recommended for JWT authentication)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Set authentication provider
                .authenticationProvider(authenticationProvider())

                // Add JWT filter before the default UsernamePasswordAuthenticationFilter
                .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class)
                
                .build();
    }

    /**
     * Configures authentication provider using DaoAuthenticationProvider.
     * DaoAuthenticationProvider retrieves user details from a database.
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        
        // Set custom UserDetailsService implementation
        authProvider.setUserDetailsService(userDetailsService);
        
        // Set password encoder (BCrypt for hashing passwords)
        authProvider.setPasswordEncoder(passwordEncoder());
        
        return authProvider;
    }

    /**
     * Defines the password encoder to be used for hashing passwords.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configures the AuthenticationManager, linking it with UserDetailsService and PasswordEncoder.
     */
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http
                .getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder())
                .and()
                .build();
    }
}
