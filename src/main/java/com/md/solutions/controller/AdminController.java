package com.md.solutions.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.md.solutions.dto.UsersDTO;
import com.md.solutions.service.UserService;

@RestController
public class AdminController {

	@Autowired
	UserService detailsService;
	
	@GetMapping("/admin")
	public ResponseEntity<List<UsersDTO>> getUser(){
		List<UsersDTO> users = detailsService.loadAllUser();
		return new ResponseEntity<List<UsersDTO>>(users, HttpStatus.OK);
	}
	
	
}
