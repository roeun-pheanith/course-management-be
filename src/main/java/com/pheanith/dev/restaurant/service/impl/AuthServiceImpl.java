package com.pheanith.dev.restaurant.service.impl;

import java.security.Principal;
import java.util.HashSet;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.pheanith.dev.restaurant.config.security.AuthUser;
import com.pheanith.dev.restaurant.config.security.JWTUtils;
import com.pheanith.dev.restaurant.config.security.RoleEnum;
import com.pheanith.dev.restaurant.dto.SigninRequest;
import com.pheanith.dev.restaurant.dto.SignupRequest;
import com.pheanith.dev.restaurant.entity.Role;
import com.pheanith.dev.restaurant.entity.User;
import com.pheanith.dev.restaurant.exception.ApiException;
import com.pheanith.dev.restaurant.repository.RoleRepository;
import com.pheanith.dev.restaurant.repository.UserRepository;
import com.pheanith.dev.restaurant.service.AuthService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final PasswordEncoder passwordEncoder;
	private final AuthenticationManager authenticationManager;
	private final JWTUtils jwtUtils;

	@Override
	public String createUser(SignupRequest signupRequest) {
		log.info(signupRequest.getPassword());
		if (userRepository.existsByUsername(signupRequest.getUsername())) {
			throw new ApiException(HttpStatus.BAD_REQUEST, "username is already token!");
		}
		if (userRepository.existsByEmail(signupRequest.getEmail())) {
			throw new ApiException(HttpStatus.BAD_REQUEST, "email is already token!");
		}
		User user = new User(signupRequest.getUsername(), signupRequest.getEmail(),
				passwordEncoder.encode(signupRequest.getPassword()));
		Set<String> strRoles = signupRequest.getRoles();

		Set<Role> roles = new HashSet<>();
		if (strRoles == null || strRoles.isEmpty()) {
			Role userRole = roleRepository.findByName(RoleEnum.USER.name())
					.orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "ROLE was not found!"));
			roles.add(userRole);
		} else {
			strRoles.forEach(role -> {
				Role otherRole = roleRepository.findByName(role)
						.orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "ROLE was not found!"));
				roles.add(otherRole);
			});
		}
		user.setRoles(roles);
		userRepository.save(user);
		return jwtUtils.generateJWTToken(signupRequest.getUsername());
	}

	@Override
	public String authenticateUser(SigninRequest signinRequest) {
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(signinRequest.getUsername(), signinRequest.getPassword()));
		SecurityContextHolder.getContext().setAuthentication(authentication);
		AuthUser userPrinciple = (AuthUser) authentication.getPrincipal();
		return jwtUtils.generateJWTToken(userPrinciple.getUsername());
	}

	@Override
	public User getByUsername(String name) {
		return userRepository.findByUsername(name)
				.orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found!"));

	}

	@Override
	public Long getUserIdByPrincipal(Principal principal) {
		String username = principal.getName();
		return userRepository.findByUsername(username)
					.map(User::getId)
					.orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found!" ));
	}

}
