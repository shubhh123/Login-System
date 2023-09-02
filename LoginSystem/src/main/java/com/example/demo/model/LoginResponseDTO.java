package com.example.demo.model;

public class LoginResponseDTO {

	private ApplicationUser user;
	private String jwt;
	
	public LoginResponseDTO() {
		super();
		// TODO Auto-generated constructor stub
	}

	public LoginResponseDTO(ApplicationUser user, String jwt) {
		super();
		this.user = user;
		this.jwt = jwt;
	}

	public ApplicationUser getUser() {
		return user;
	}

	public void setUser(ApplicationUser user) {
		this.user = user;
	}

	public String getJwt() {
		return jwt;
	}

	public void setJwt(String jwt) {
		this.jwt = jwt;
	}
	
	
}
