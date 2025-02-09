package com.md.solutions.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.md.solutions.config.CustomPasswordEncoder;
import com.md.solutions.config.JwtAuthenticationFilter;
import com.md.solutions.service.UserService;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@RestController
public class LoginController {
	
	@Autowired
	UserService detailsService;
	
	@Autowired
	CustomPasswordEncoder passwordEncoder;

	@PostMapping("/login")
	public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> loginDetails) {
		UserDetails dbUser = detailsService. loadUserByUsername(loginDetails.get("username"));
		Map<String, String> map = new HashMap<String, String>();
		if( passwordEncoder.matches(loginDetails.get("password"), dbUser.getPassword())) {
			Map<String, Object> claims = new HashMap<String, Object>();
			claims.put("roles", dbUser.getAuthorities());
			claims.put("subject", dbUser.getUsername());
			String token = Jwts.builder().addClaims(claims).setSubject(dbUser.getUsername()).setIssuedAt(new Date())
					.setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
					.signWith(Keys.hmacShaKeyFor(JwtAuthenticationFilter.SECRET_KEY.getBytes())).compact();
			map.put("access_token", token);
			return new ResponseEntity<Map<String, String>>(map, HttpStatus.OK);
		}else {
			map.put("error", "InvalidCredentials");
			return new ResponseEntity<Map<String, String>>(map, HttpStatus.BAD_REQUEST);
		}
	}
	
}
