package com.spring.vaidya.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.spring.vaidya.entity.AuthRequest;
import com.spring.vaidya.entity.LoginRequest;
import com.spring.vaidya.entity.LoginResponse;
import com.spring.vaidya.entity.User;
import com.spring.vaidya.exception.AuthenticationFailedException;
import com.spring.vaidya.exception.UserNotFoundException;
import com.spring.vaidya.jwt.JwtUtils;
import com.spring.vaidya.repo.UserRepository;
import com.spring.vaidya.service.UserService;

import jakarta.validation.Valid;

/**
 * Controller for handling user authentication, registration, and JWT-based authentication.
 */
@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "http://localhost:5173") // Allow frontend access from specified origin
public class UserController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtils jwtUtils;

    /**
     * Registers a new user.
     * 
     * @param user The User object containing registration details.
     * @return ResponseEntity with a success message.
     */
    @PostMapping("/new")
    public ResponseEntity<?> registerUser(@Valid @RequestBody User user) {
        try {
            userService.registerUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully!");
        } catch (Exception e) {
            throw new RuntimeException("User registration failed", e);
        }
    }

    /**
     * Authenticates a user and generates a JWT token.
     * 
     * @param authRequest The authentication request containing username and password.
     * @return ResponseEntity with the JWT token.
     * @throws AuthenticationFailedException if authentication fails.
     */
    @PostMapping("/authenticate")
    public ResponseEntity<String> authenticateUser(@RequestBody AuthRequest authRequest) {
        try {
            // Attempt authentication with provided username and password
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
            );

            // Store authentication details in security context
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Generate JWT token
            String jwt = jwtUtils.generateJwtToken(authRequest.getUsername());
            return ResponseEntity.ok("Bearer " + jwt);
        } catch (Exception e) {
            throw new AuthenticationFailedException("Invalid username or password");
        }
    }

    /**
     * Welcome endpoint for testing API accessibility.
     * 
     * @return ResponseEntity with a welcome message.
     */
    @GetMapping("/welcome")
    public ResponseEntity<String> welcome() {
        return ResponseEntity.ok("Welcome to the JWT-secured API!");
    }

    /**
     * A protected route that requires JWT authentication.
     * 
     * @return ResponseEntity with a success message.
     */
    @GetMapping("/protected")
    public ResponseEntity<String> protectedRoute() {
        return ResponseEntity.ok("This is a protected route, only accessible with a valid JWT.");
    }

    /**
     * Authenticates a doctor and generates a JWT token if credentials are valid.
     * 
     * @param loginRequest The login request containing email and password.
     * @return ResponseEntity with the JWT token and doctor details.
     * @throws UserNotFoundException if the doctor is not found.
     * @throws AuthenticationFailedException if authentication fails or the doctor is not verified.
     */
    @PostMapping("/login")
    public ResponseEntity<?> loginDoctor(@RequestBody LoginRequest loginRequest) {
        // Find doctor by email
        User doctor = userRepository.findByUserEmailIgnoreCase(loginRequest.getUserEmail())
                .orElseThrow(() -> new UserNotFoundException("Doctor not found with email: " + loginRequest.getUserEmail()));

        // Check if the password matches
        boolean isPasswordMatch = passwordEncoder.matches(loginRequest.getPassword(), doctor.getPassword());
        if (!isPasswordMatch) {
            throw new AuthenticationFailedException("Invalid credentials");
        }

        // Ensure the doctor account is verified
        if (!doctor.isEnabled()) {
            throw new AuthenticationFailedException("Doctor is not verified");
        }

        // Generate JWT token
        String jwt = jwtUtils.generateJwtToken(doctor.getUserEmail());

        // Create a response containing the JWT, doctor name, and ID
        LoginResponse response = new LoginResponse(
                jwt,
                doctor.getFullName(),
                doctor.getUserId()
        );

        return ResponseEntity.ok(response);
    }
}
