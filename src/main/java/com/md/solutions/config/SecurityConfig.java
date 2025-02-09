package com.md.solutions.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.md.solutions.service.UserService;

@Configuration
public class SecurityConfig {

	private final UserService userDetailsService;
	
	public SecurityConfig(UserService userDetailsService) {
		this.userDetailsService = userDetailsService;
	}
	
	@Autowired
	CustomPasswordEncoder passwordEncoder;
	
	@Bean
	public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
		return authentication -> {
			 String username = authentication.getName();
		     String password = (String) authentication.getCredentials();

		        UserDetails user = userDetailsService.loadUserByUsername(username);
		        
		        if(passwordEncoder.matches(password, user.getPassword())) {
		        	Authentication userAuthentication = new UsernamePasswordAuthenticationToken(username, password, user.getAuthorities());
		        	SecurityContextHolder.getContext().setAuthentication(userAuthentication);
		            return SecurityContextHolder.getContext().getAuthentication();
		        } else {
		            throw new BadCredentialsException("Invalid credentials");
		        }
		};
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		
		http.csrf(CsrfConfigurer::disable);
		http.authorizeHttpRequests(((authorizeHttpRequests) ->
 				authorizeHttpRequests
 					.requestMatchers("/login", "/register").permitAll()
 					.requestMatchers("/admin").hasRole("ADMIN")
 					.requestMatchers("/user").hasAnyRole("USER")
 					.anyRequest().authenticated()
 					));
		http.addFilterBefore(new JwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
		return http.build();
	}
	
}



