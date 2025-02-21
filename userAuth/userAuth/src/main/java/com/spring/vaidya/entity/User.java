package com.spring.vaidya.entity;

import java.time.LocalTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;
    
    @NotBlank(message ="full name is required")
    private String fullName;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String userEmail;
    private String specialization;
    private String qualification;
    private int experience;
    private String address;
    private String gender;
    
    @NotBlank(message = "Phone number is required")
    @Size(min = 10, max = 10, message = "Phone number must be 10 digits")
    @Pattern(regexp = "\\d{10}", message = "Phone number must contain only digits")
    private String phoneNumber;
    
    @NotBlank(message = "Password is required"	)
    private String password;
    private String diseases;
    private String clinicName;
    private LocalTime openTime;
    private LocalTime closeTime;
    private boolean isEnabled = false;

    private Integer roleId = 1; // New field for role ID
    
    @NotBlank(message = "Aadhar number is required")
    @Size(min = 12, max = 12, message = "Aadhar number must be 12 digits")
    @Pattern(regexp = "\\d{12}", message = "Aadhar number must contain only digits")
    private String aadharNo; // New field for Aadhar number
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public String getUserEmail() {
		return userEmail;
	}
	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}
	public String getSpecialization() {
		return specialization;
	}
	public void setSpecialization(String specialization) {
		this.specialization = specialization;
	}
	public String getQualification() {
		return qualification;
	}
	public void setQualification(String qualification) {
		this.qualification = qualification;
	}
	public int getExperience() {
		return experience;
	}
	public void setExperience(int experience) {
		this.experience = experience;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getDiseases() {
		return diseases;
	}
	public void setDiseases(String diseases) {
		this.diseases = diseases;
	}
	public String getClinicName() {
		return clinicName;
	}
	public void setClinicName(String clinicName) {
		this.clinicName = clinicName;
	}
	public LocalTime getOpenTime() {
		return openTime;
	}
	public void setOpenTime(LocalTime openTime) {
		this.openTime = openTime;
	}
	public LocalTime getCloseTime() {
		return closeTime;
	}
	public void setCloseTime(LocalTime closeTime) {
		this.closeTime = closeTime;
	}
	public boolean isEnabled() {
		return isEnabled;
	}
	public void setEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
	}
	public Integer getRoleId() {
		return roleId;
	}
	public void setRoleId(Integer roleId) {
		this.roleId = roleId;
	}
	public String getAadharNo() {
		return aadharNo;
	}
	public void setAadharNo(String aadharNo) {
		this.aadharNo = aadharNo;
	}
	public User(Long userId, String fullName, String userEmail, String specialization, String qualification,
			int experience, String address, String gender, String phoneNumber, String password, String diseases,
			String clinicName, LocalTime openTime, LocalTime closeTime, boolean isEnabled, Integer roleId,
			String aadharNo) {
		super();
		this.userId = userId;
		this.fullName = fullName;
		this.userEmail = userEmail;
		this.specialization = specialization;
		this.qualification = qualification;
		this.experience = experience;
		this.address = address;
		this.gender = gender;
		this.phoneNumber = phoneNumber;
		this.password = password;
		this.diseases = diseases;
		this.clinicName = clinicName;
		this.openTime = openTime;
		this.closeTime = closeTime;
		this.isEnabled = isEnabled;
		this.roleId = roleId;
		this.aadharNo = aadharNo;
	}
	public User() {
		super();
	}
	public User(String userEmail2, String password2, Object object) {
		// TODO Auto-generated constructor stub
	}
    
  
}

