package com.example.demo.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@CrossOrigin("*")  //useful in building front end, that way you will not be blocked by CORS
public class UserCotroller {

	
	@GetMapping("/check/status")
	public String checkStatus() {
		return "User level access granted";
		}
}
