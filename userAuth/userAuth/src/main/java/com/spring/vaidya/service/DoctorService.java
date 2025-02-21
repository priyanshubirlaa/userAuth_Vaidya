package com.spring.vaidya.service;

import org.springframework.http.ResponseEntity;
import com.spring.vaidya.entity.User;

/**
 * Service interface for managing doctor-related operations.
 */
public interface DoctorService {

    /**
     * Saves a new doctor into the system.
     *
     * @param doctor The doctor entity to be saved.
     * @return A ResponseEntity containing success or error message.
     */
    ResponseEntity<?> saveDoctor(User doctor);

    /**
     * Confirms the doctor's email using the provided confirmation token.
     *
     * @param confirmTokenDoctor The token used for email verification.
     * @return A ResponseEntity indicating success or failure.
     */
    ResponseEntity<?> confirmEmail(String confirmTokenDoctor);
    
    /**
     * Retrieves a doctor by their email.
     *
     * @param email The email of the doctor.
     * @return The User entity representing the doctor.
     */
    User getDoctorByEmail(String email);
}
	