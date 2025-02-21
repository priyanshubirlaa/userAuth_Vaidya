package com.spring.vaidya.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.spring.vaidya.entity.ErrorResponse;

import java.time.LocalDateTime;

/**
 * EmailService class handles sending emails asynchronously.
 * It includes functionality for general emails and password reset emails.
 */
@Service("emailService")
public class EmailService {

    // Logger instance for logging errors and info
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    
    private final JavaMailSender javaMailSender;

    /**
     * Constructor for injecting JavaMailSender dependency.
     * @param javaMailSender JavaMailSender instance for email handling.
     */
    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    /**
     * Sends a generic email asynchronously.
     * @param email The SimpleMailMessage object containing email details.
     * @return ErrorResponse indicating success or failure of the email sending process.
     */
    @Async
    public ErrorResponse sendEmail(SimpleMailMessage email) {
        try {
            javaMailSender.send(email);
            return new ErrorResponse(LocalDateTime.now(), 200, "SUCCESS", "Email sent successfully");
        } catch (MailException e) {
            logger.error("Error sending email: {}", e.getMessage());
            return new ErrorResponse(LocalDateTime.now(), 500, "MAIL_ERROR", "Failed to send email");
        }
    }

    /**
     * Sends a password reset email containing a unique reset link.
     * @param email The recipient's email address.
     * @param token The password reset token for verification.
     * @return ErrorResponse indicating success or failure of the password reset email.
     */
    public ErrorResponse sendPasswordResetEmail(String email, String token) {
        // Construct password reset link (Modify the URL based on your frontend setup)
        String resetLink = "http://yourfrontend.com/reset-password?token=" + token;

        // Prepare the email message
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Password Reset Request");
        message.setText("Click the link to reset your password: " + resetLink);

        try {
            javaMailSender.send(message);
            return new ErrorResponse(LocalDateTime.now(), 200, "SUCCESS", "Password reset email sent successfully");
        } catch (MailException e) {
            logger.error("Error sending password reset email: {}", e.getMessage());
            return new ErrorResponse(LocalDateTime.now(), 500, "MAIL_ERROR", "Failed to send password reset email");
        }
    }
}
