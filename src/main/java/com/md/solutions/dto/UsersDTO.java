package com.md.solutions.dto;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UsersDTO {

	private Long id;

	private String username;
	private String password;

	private Set<RolesDTO> roles; 
}
