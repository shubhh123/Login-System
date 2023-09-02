package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.RegistrationDTO;
import com.example.demo.model.ApplicationUser;
import com.example.demo.model.LoginResponseDTO;
import com.example.demo.services.AuthenticationService;

@RestController
@RequestMapping("/auth")
@CrossOrigin("*")
public class AuthenticationController {
	
	private AuthenticationService authService;
	
	@Autowired
	public AuthenticationController(AuthenticationService authService) {
		this.authService = authService;
	}
	
	
	@PostMapping("/register")
	public ApplicationUser registerUser(@RequestBody RegistrationDTO registrationDTO) {
		return authService.registerUser(registrationDTO.getUsername(), registrationDTO.getPassword());
	}
	
	@PostMapping("/login")
	public LoginResponseDTO loginUser(@RequestBody RegistrationDTO registrationDTO) {
		return authService.loginUser(registrationDTO.getUsername (), registrationDTO.getPassword ());
	}
}
