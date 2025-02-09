package com.md.solutions.config;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	public static final String SECRET_KEY = "your-secryour-secret-key-for-jwtet-key-fyour-secret-key-for-jwtor-jwt"; // Use
//	private final UserService userDetailsService;
//
//	public JwtAuthenticationFilter(UserService userDetailsService) {
//		this.userDetailsService = userDetailsService;
//	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String token = resolveToken(request);
		if (token != null && validateToken(token)) {
			Authentication authentication = getAuthentication(token, request);
			if (authentication != null) {
				SecurityContextHolder.getContext().setAuthentication(authentication);
			}
		}
		filterChain.doFilter(request, response);
	}

	private String resolveToken(HttpServletRequest request) {
		String bearerToken = request.getHeader("Authorization");
		if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7);
		}
		return null;
	}

	private boolean validateToken(String token) {
		try {
			Jwts.parserBuilder().setSigningKey(Keys.hmacShaKeyFor(SECRET_KEY.getBytes())).build().parseClaimsJws(token);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private Authentication getAuthentication(String token, HttpServletRequest request) {
		Claims claims = Jwts.parserBuilder().setSigningKey(Keys.hmacShaKeyFor(SECRET_KEY.getBytes())).build()
				.parseClaimsJws(token).getBody();

		String username = claims.getSubject();
		if (username != null) {
			List<Map<String, String>> roles = (List<Map<String, String>>) claims.get("roles", List.class);

			for (Map<String, String> roleMap : roles) {
				for (String key : roleMap.keySet()) {
					System.out.println(key + " ::  " + roleMap.get(key));
				}
			}

			List<SimpleGrantedAuthority> authorities = roles.stream()
					.map(roleMap -> new SimpleGrantedAuthority(roleMap.get("authority"))).collect(Collectors.toList());

			UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username, null,
					authorities);

			authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

			return authentication;
		}
		return null;
	}
}
