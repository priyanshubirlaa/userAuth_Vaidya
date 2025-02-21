package com.spring.vaidya.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spring.vaidya.entity.ErrorResponse;
import com.spring.vaidya.entity.ForgotPasswordRequest;
import com.spring.vaidya.entity.ResetPasswordRequest;
import com.spring.vaidya.service.UserService;

/**
 * Controller for handling authentication-related operations such as 
 * forgot password and reset password.
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    /**
     * Handles the "Forgot Password" functionality.
     * 
     * @param request Contains the user's email for password reset initiation.
     * @return ResponseEntity indicating success or failure.
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        // Call the UserService to initiate the forgot password process.
        Object response = userService.initiateForgotPassword(request.getEmail());

        // If the response is an error, return an appropriate HTTP status code.
        if (response instanceof ErrorResponse) {
            ErrorResponse errorResponse = (ErrorResponse) response;
            return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);
        }

        // If successful, return a success message.
        return ResponseEntity.ok("Password reset email sent successfully.");
    }

    /**
     * Handles the "Reset Password" functionality.
     * 
     * @param request Contains the reset token and the new password.
     * @return ResponseEntity indicating success or failure.
     */
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        // Call the UserService to reset the password using the provided token and new password.
        Object response = userService.resetPassword(request.getToken(), request.getNewPassword());

        // If the response is an error, return an appropriate HTTP status code.
        if (response instanceof ErrorResponse) {
            ErrorResponse errorResponse = (ErrorResponse) response;
            return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);
        }

        // If successful, return a success message.
        return ResponseEntity.ok("Password reset successful.");
    }
}
