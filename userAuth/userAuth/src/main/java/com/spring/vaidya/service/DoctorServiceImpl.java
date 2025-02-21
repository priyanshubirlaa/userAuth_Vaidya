package com.spring.vaidya.service;

import java.time.LocalDateTime;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.spring.vaidya.entity.ConfirmTokenDoctor;
import com.spring.vaidya.entity.ErrorResponse;
import com.spring.vaidya.entity.User;
import com.spring.vaidya.repo.ConfirmTokenDoctorRepo;
import com.spring.vaidya.repo.DoctorRepository;

/**
 * Implementation of DoctorService interface.
 * Handles doctor registration, email confirmation, and data retrieval.
 */
@Service
public class DoctorServiceImpl implements DoctorService {

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private ConfirmTokenDoctorRepo confirmTokenDoctorRepo;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Regular expressions for validation
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"; // Valid email pattern
    private static final String AADHAAR_REGEX = "^[0-9]{12}$"; // Aadhaar must be 12 digits
    private static final String PHONE_REGEX = "\\d{10}"; // Phone number must be exactly 10 digits

    /**
     * Registers a doctor after validating the input details.
     * 
     * @param doctor The doctor entity containing registration details.
     * @return ResponseEntity with status and message.
     */
    @Override
    public ResponseEntity<ErrorResponse> saveDoctor(User doctor) {
        // Validate email format
        if (!Pattern.matches(EMAIL_REGEX, doctor.getUserEmail())) {
            return ResponseEntity.badRequest().body(
                new ErrorResponse(LocalDateTime.now(), 400, "INVALID_EMAIL", "Invalid email format!")
            );
        }

        // Validate Aadhaar number format
        if (!Pattern.matches(AADHAAR_REGEX, doctor.getAadharNo())) {
            return ResponseEntity.badRequest().body(
                new ErrorResponse(LocalDateTime.now(), 400, "INVALID_AADHAAR", "Aadhaar number must be exactly 12 digits!")
            );
        }

        // Validate phone number format
        if (!doctor.getPhoneNumber().matches(PHONE_REGEX)) {
            return ResponseEntity.badRequest().body(
                new ErrorResponse(LocalDateTime.now(), 400, "INVALID_PHONE", "Phone number must be exactly 10 digits!")
            );
        }

        // Check if email already exists in the system
        if (doctorRepository.existsByUserEmail(doctor.getUserEmail())) {
            return ResponseEntity.badRequest().body(
                new ErrorResponse(LocalDateTime.now(), 400, "EMAIL_EXISTS", "Error: Email is already in use!")
            );
        }

        // Encrypt the password before saving the doctor entity
        doctor.setPassword(passwordEncoder.encode(doctor.getPassword()));
        doctorRepository.save(doctor);

        // Generate a confirmation token for email verification
        ConfirmTokenDoctor confirmationToken = new ConfirmTokenDoctor(doctor);
        confirmTokenDoctorRepo.save(confirmationToken);

        // Prepare and send the verification email
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(doctor.getUserEmail());
        mailMessage.setSubject("Complete Registration!");
        mailMessage.setText("To confirm your account, please click here : "
                + "https://vault1-production-7c73.up.railway.app/doctor/confirm-account?token=" 
                + confirmationToken.getConfirmationToken());
        emailService.sendEmail(mailMessage);

        // Return response indicating that verification email has been sent
        return ResponseEntity.ok(new ErrorResponse(LocalDateTime.now(), 200, "VERIFICATION_EMAIL_SENT", 
            "Verify email by the link sent to your email address"));
    }

    /**
     * Confirms the doctor's email by verifying the provided token.
     * 
     * @param confirmationToken The token sent to the doctor's email.
     * @return ResponseEntity indicating success or failure of email verification.
     */
    @Override
    public ResponseEntity<ErrorResponse> confirmEmail(String confirmationToken) {
        // Retrieve the confirmation token from the database
        ConfirmTokenDoctor token = confirmTokenDoctorRepo.findByConfirmTokenDoctor(confirmationToken);

        // If token exists, activate the doctor account
        if (token != null) {
            User doctor = doctorRepository.findByUserEmailIgnoreCase(token.getDoctorEntity().getUserEmail());
            doctor.setEnabled(true); // Enable the doctor's account
            doctorRepository.save(doctor);

            return ResponseEntity.ok(new ErrorResponse(LocalDateTime.now(), 200, "EMAIL_VERIFIED", 
                "Email verified successfully!"));
        }

        // Return error response if token is invalid
        return ResponseEntity.badRequest().body(
            new ErrorResponse(LocalDateTime.now(), 400, "INVALID_TOKEN", "Error: Couldn't verify email")
        );
    }

    /**
     * Retrieves a doctor entity by email.
     * 
     * @param email The email of the doctor.
     * @return The User entity representing the doctor.
     */
    @Override
    public User getDoctorByEmail(String email) {
        return doctorRepository.findByUserEmail(email);
    }
}
