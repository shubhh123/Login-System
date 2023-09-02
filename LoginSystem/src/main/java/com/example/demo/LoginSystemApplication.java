package com.example.demo;

import java.util.HashSet;
import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.demo.model.ApplicationUser;
import com.example.demo.model.Role;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;

@SpringBootApplication
public class LoginSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(LoginSystemApplication.class, args);
	}
	
	
	//Runs everytime during application startup
	@Bean
	CommandLineRunner run(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
		return args -> {
			
			//If the adminRole has already created, that means the user role  must have existed and 
			//also the adminUser must exist....
			
			if(roleRepository.findByAuthority("ROLE").isPresent()) {
				return; //exit out of this method
			}
			
			//adminRole object will have the ID and other attributes populated with the values that 
			//were assigned during the database insertion.
			
			
			
			Role adminRole = roleRepository.save(new Role("ADMIN"));
			roleRepository.save(new Role("USER"));
			
			
			Set<Role> roles = new HashSet<Role>();
			roles.add(adminRole);
			
			
			//Created a bunch of admins. 
			ApplicationUser adminUser = new ApplicationUser(1,"admin",passwordEncoder.encode("password"),roles);
			userRepository.save(adminUser);
		};
	}
}
