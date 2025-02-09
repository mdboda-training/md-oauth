package com.md.solutions.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.md.solutions.config.CustomPasswordEncoder;
import com.md.solutions.dto.UsersDTO;
import com.md.solutions.service.UserService;

@RestController
public class RegisterController {

	@Autowired
	UserService detailsService;
	
	@Autowired
	CustomPasswordEncoder passwordEncoder;

	@PostMapping("/register")
	public ResponseEntity<UsersDTO> resigterUser(@RequestBody UsersDTO userDto) {
		userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
		UsersDTO dbUser = detailsService.register(userDto);
		return new ResponseEntity<UsersDTO>(dbUser, HttpStatus.CREATED);
	}

}
