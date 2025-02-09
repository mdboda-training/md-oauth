package com.md.solutions.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.md.solutions.dto.RolesDTO;
import com.md.solutions.dto.UsersDTO;
import com.md.solutions.entity.Roles;
import com.md.solutions.entity.Users;
import com.md.solutions.repository.RolesRepository;
import com.md.solutions.repository.UserDetailsRepository;

@Service
public class UserService implements UserDetailsService {

	@Autowired
	UserDetailsRepository userRepository;

	@Autowired
	RolesRepository rolesRepository;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		String ROLE = "ROLE_";
		Optional<Users> optionalUser = userRepository.getUserByName(username);
		if (optionalUser.isPresent()) {
			Users myUser = optionalUser.get();
			Set<GrantedAuthority> authorities = myUser.getRoles().stream()
					.map(role -> new SimpleGrantedAuthority(ROLE + role.getName())).collect(Collectors.toSet());
			UserDetails userDetails = new User(myUser.getUsername(), myUser.getPassword(),
					myUser.getRoles().stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName())) 
							.collect(Collectors.toList()));
			Map<String, Object> claims = new HashMap<String, Object>();

			claims.put("roles", userDetails.getAuthorities());
			claims.put("subject", userDetails.getUsername());
//    		.setSubject(username)
//            .setIssuedAt(new Date())
//            .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
//    		Jwts.builder().addClaims(claims).setSubject(username)
//    		.setIssuedAt(new Date()).setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
//    		.signWith(Keys.hmacShaKeyFor(JwtAuthenticationFilter.SECRET_KEY.getBytes())).compact();
			Authentication authentication = new UsernamePasswordAuthenticationToken(myUser.getUsername(), myUser.getPassword(),
					authorities);
			SecurityContextHolder.getContext().setAuthentication(authentication);
//    		SecurityContextHolder.getContext().getAuthentication().setAuthenticated(true);
			return userDetails;
		} else {
			throw new UsernameNotFoundException("User not found");
		}
	}

	public UsersDTO register(UsersDTO userDto) {

		Set<Roles> roles = userDto.getRoles().stream().map(roleName -> {
			Roles role = rolesRepository.findByRoleName(roleName.getName());
			if (role == null) {
				role = new Roles();
				role.setName(roleName.getName());
			}
			return role;
		}).collect(Collectors.toSet());
		Users user = new Users();
		BeanUtils.copyProperties(userDto, user);
		user.setRoles(roles);

		for (Roles role : roles) {
			if (role.getId() == null) {
				rolesRepository.save(role);
			}
		}
//		user.setPassword(passwordEncoder.encode(userDto.getPassword()));
		user = userRepository.save(user);
		BeanUtils.copyProperties(user, userDto);
		Set<RolesDTO> rolesDTO = user.getRoles().stream().map(role -> new RolesDTO(role.getId(), role.getName(),null)).collect(Collectors.toSet());
		userDto.setRoles(rolesDTO);
		return userDto;
	}

	public List<UsersDTO> loadAllUser() {
		List<Users> list = userRepository.findAll(); 
		List<UsersDTO> result = new ArrayList<UsersDTO>();
		for(Users user: list) {
			UsersDTO userDTO = new UsersDTO();
			BeanUtils.copyProperties(user, userDTO);
			result.add(userDTO);
		}
		return result;
	}

}
