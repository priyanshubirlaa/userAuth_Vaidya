package com.spring.vaidya.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.spring.vaidya.entity.User;
import com.spring.vaidya.exception.ResourceNotFoundException;
import com.spring.vaidya.repo.DoctorRepository;
import com.spring.vaidya.service.DoctorServiceImpl;

import jakarta.validation.Valid;

/**
 * Controller for managing doctor-related operations such as registration, 
 * account confirmation, and retrieval of doctor details.
 */
@RestController
@RequestMapping("/doctor")
@CrossOrigin(origins = "http://localhost:5173/") // Allow frontend access from specified origin
public class DoctorController {

    @Autowired
    private DoctorServiceImpl doctorService;

    @Autowired
    private DoctorRepository doctorRepository;

    /**
     * Registers a new doctor.
     * 
     * @param doctor The User object representing the doctor.
     * @return ResponseEntity containing the result of the registration.
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerDoctor(@Valid @RequestBody User doctor) {
        try {
            return doctorService.saveDoctor(doctor);
        } catch (Exception e) {
            throw new RuntimeException("Failed to register doctor", e);
        }
    }

    /**
     * Confirms a doctor's email account using a confirmation token.
     * 
     * @param confirmationToken The token sent to the doctor's email.
     * @return ResponseEntity indicating whether the confirmation was successful.
     */
    @GetMapping("/confirm-account")
    public ResponseEntity<?> confirmDoctorAccount(@RequestParam("token") String confirmationToken) {
        return doctorService.confirmEmail(confirmationToken);
    }

    /**
     * Retrieves a list of all registered doctors.
     * 
     * @return ResponseEntity containing the list of doctors.
     * @throws ResourceNotFoundException if no doctors are found.
     */
    @GetMapping("/all")
    public ResponseEntity<List<User>> getAllDoctors() {
        List<User> doctors = doctorRepository.findAll();
        if (doctors.isEmpty()) {
            throw new ResourceNotFoundException("No doctors found");
        }
        return ResponseEntity.ok(doctors);
    }

    /**
     * Retrieves a doctor's details based on their unique ID.
     * 
     * @param doctorId The ID of the doctor.
     * @return ResponseEntity containing the doctor's details.
     * @throws ResourceNotFoundException if the doctor is not found.
     */
    @GetMapping("/{doctorId}")
    public ResponseEntity<User> getDoctorById(@PathVariable Long doctorId) {
        User doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with ID: " + doctorId));
        return ResponseEntity.ok(doctor);
    }

    /**
     * Retrieves a doctor's details based on their email.
     * 
     * @param email The email of the doctor.
     * @return ResponseEntity containing the doctor's details.
     * @throws ResourceNotFoundException if the doctor is not found.
     */
    @GetMapping("/email")
    public ResponseEntity<User> getDoctorByEmail(@RequestParam String email) {
        User doctor = doctorService.getDoctorByEmail(email);
        if (doctor == null) {
            throw new ResourceNotFoundException("Doctor not found with email: " + email);
        }
        return ResponseEntity.ok(doctor);
    }
}
