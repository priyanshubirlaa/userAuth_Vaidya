package com.spring.vaidya.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.spring.vaidya.entity.ErrorResponse;
import com.spring.vaidya.entity.PasswordResetToken;
import com.spring.vaidya.entity.User;
import com.spring.vaidya.repo.PasswordResetTokenRepository;
import com.spring.vaidya.repo.UserRepository;

/**
 * Service class for user-related operations such as registration,
 * password reset, and authentication.
 */
@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Constructor-based dependency injection for required components.
     * 
     * @param userRepository The repository for user-related database operations.
     * @param passwordEncoder Encoder for securing passwords.
     */
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Registers a new user.
     * 
     * @param user User object containing registration details.
     * @return Registered user object or error response if registration fails.
     */
    public Object registerUser(User user) {
        try {
            // Check if the email is already registered
            if (userRepository.existsByUserEmail(user.getUserEmail())) {
                return new ErrorResponse(LocalDateTime.now(), 400, "USER_EXISTS", "Email is already registered");
            }

            // Validate phone number (should be exactly 10 digits)
            if (user.getPhoneNumber() == null || !user.getPhoneNumber().matches("\\d{10}")) {
                return new ErrorResponse(LocalDateTime.now(), 400, "INVALID_PHONE", "Phone number must be exactly 10 digits");
            }

            logger.info("Registering user: {}", user.getUserEmail());  // Log user email before saving

            // Encrypt the user's password before storing it
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            User savedUser = userRepository.save(user);
            
            logger.info("User registered successfully with ID: {}", savedUser.getUserId());
            
            return savedUser;
        } catch (Exception e) {
            logger.error("Error registering user: {}", e.getMessage());
            return new ErrorResponse(LocalDateTime.now(), 500, "REGISTER_ERROR", "Failed to register user");
        }
    }

    /**
     * Initiates the password reset process by generating a reset token and sending an email.
     * 
     * @param email The email address of the user requesting a password reset.
     * @return Success message or error response if user not found.
     */
//    public Object initiateForgotPassword(String email) {
//        try {
//            Optional<User> optionalUser = userRepository.findByUserEmailIgnoreCase(email);
//
//            // Check if the email exists in the database
//            if (optionalUser.isEmpty()) {
//                logger.warn("Forgot password request failed: No user found for email {}", email);
//                return new ErrorResponse(LocalDateTime.now(), 404, "USER_NOT_FOUND", "No user found with this email");
//            }
//
//            User user = optionalUser.get();
//            String token = UUID.randomUUID().toString(); // Generate a random reset token
//
//            // Create and store the password reset token
//            PasswordResetToken resetToken = new PasswordResetToken();
//            resetToken.setToken(token);
//            resetToken.setUser(user);
//            resetToken.setExpiryDate(LocalDateTime.now().plusMinutes(30)); // Set expiration time
//            tokenRepository.save(resetToken);
//
//            logger.info("Sending password reset email to: {}", email);
//            emailService.sendPasswordResetEmail(email, token);
//
//            return "Password reset email sent successfully!";
//        } catch (Exception e) {
//            logger.error("Error initiating password reset: {}", e.getMessage());
//            return new ErrorResponse(LocalDateTime.now(), 500, "PASSWORD_RESET_ERROR", "Failed to send password reset email");
//        }
//    }

    public Object initiateForgotPassword(String email) {
        Logger logger = LoggerFactory.getLogger(getClass());

        try {
            Optional<User> optionalUser = userRepository.findByUserEmailIgnoreCase(email);

            // Check if the email exists in the database
            if (optionalUser.isEmpty()) {
                logger.warn("Forgot password request failed: No user found for email {}", email);
                return new ErrorResponse(LocalDateTime.now(), 404, "USER_NOT_FOUND", "No user found with this email");
            }

            User user = optionalUser.get();
            String token = UUID.randomUUID().toString();
            LocalDateTime expiryDate = LocalDateTime.now().plusMinutes(30);

            // Check if a token already exists for the user
            PasswordResetToken existingToken = tokenRepository.findByUser(user);

            if (existingToken != null) {
                // Update existing token instead of creating a new one
                existingToken.setToken(token);
                existingToken.setExpiryDate(expiryDate);
            } else {
                // Create a new token entry if it doesn't exist
                existingToken = new PasswordResetToken();
                existingToken.setUser(user);
                existingToken.setToken(token);
                existingToken.setExpiryDate(expiryDate);
            }

            // Save the updated/new token
            tokenRepository.save(existingToken);

            // Send reset email
            logger.info("Sending password reset email to: {}", email);
            emailService.sendPasswordResetEmail(email, token);

            return new ErrorResponse(LocalDateTime.now(), 200, "EMAIL_SENT", "Password reset email sent successfully!");
        } catch (Exception e) {
            logger.error("Error initiating password reset: {}", e.getMessage());
            return new ErrorResponse(LocalDateTime.now(), 500, "PASSWORD_RESET_ERROR", "Failed to send password reset email");
        }
    }
    /**
     * Resets the user's password using a valid reset token.
     * 
     * @param token The password reset token provided by the user.
     * @param newPassword The new password to be set.
     * @return Success message or error response if token is invalid or expired.
     */
    public Object resetPassword(String token, String newPassword) {
        try {
            Optional<PasswordResetToken> optionalResetToken = tokenRepository.findByToken(token);

            // Check if the provided token exists
            if (optionalResetToken.isEmpty()) {
                return new ErrorResponse(LocalDateTime.now(), 400, "INVALID_TOKEN", "Invalid or expired token");
            }

            PasswordResetToken resetToken = optionalResetToken.get();

            // Check if the token has expired
            if (resetToken.isExpired()) {
                return new ErrorResponse(LocalDateTime.now(), 400, "TOKEN_EXPIRED", "Reset token has expired");
            }

            // Retrieve user associated with the token
            User user = resetToken.getUser();

            // Encrypt and update the new password
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);

            // Delete the used token after successful reset
            tokenRepository.delete(resetToken);

            logger.info("Password successfully reset for user: {}", user.getUserEmail());
            return "Password reset successfully!";
        } catch (Exception e) {
            logger.error("Error resetting password: {}", e.getMessage());
            return new ErrorResponse(LocalDateTime.now(), 500, "RESET_PASSWORD_ERROR", "Failed to reset password");
        }
    }

    /**
     * Finds a user by email (case-insensitive).
     * 
     * @param username The email address of the user.
     * @return An optional user object if found.
     */
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUserEmailIgnoreCase(username);
    }
}
