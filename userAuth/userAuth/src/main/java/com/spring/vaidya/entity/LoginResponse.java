package com.spring.vaidya.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponse {
	
	 private String token;
	    private String fullName;
	    private Long userId;
	    
		public LoginResponse(String token, String fullName, Long userId) {
			super();
			this.token = token;
			this.fullName = fullName;
			this.userId = userId;
		}

		public String getToken() {
			return token;
		}

		public void setToken(String token) {
			this.token = token;
		}

		public String getFullName() {
			return fullName;
		}

		public void setFullName(String fullName) {
			this.fullName = fullName;
		}

		public Long getUserId() {
			return userId;
		}

		public void setUserId(Long userId) {
			this.userId = userId;
		}
		
		

	    


}