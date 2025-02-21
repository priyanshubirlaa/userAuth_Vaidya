package com.spring.vaidya.entity;
public class ForgotPasswordRequest {
    private String email;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public String toString() {
		return "ForgotPasswordRequest [email=" + email + "]";
	}

	public ForgotPasswordRequest(String email) {
		super();
		this.email = email;
	}

	public ForgotPasswordRequest() {
		super();
		// TODO Auto-generated constructor stub
	}
    
}
