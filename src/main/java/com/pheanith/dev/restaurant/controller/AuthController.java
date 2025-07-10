package com.pheanith.dev.restaurant.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pheanith.dev.restaurant.dto.SigninRequest;
import com.pheanith.dev.restaurant.dto.SignupRequest;
import com.pheanith.dev.restaurant.service.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Slf4j
public class AuthController {

	private final AuthService authService;

	@PostMapping("/signup")
	public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signupRequest) {
		log.info(signupRequest.getPassword());
		String jwt = authService.createUser(signupRequest);
		log.info("this is jwt " +jwt);
		HttpHeaders responseHeader = new HttpHeaders();
		responseHeader.set("Authorization", "Bearer " + jwt);
		return ResponseEntity.ok().headers(responseHeader).build();
	}

	@PostMapping("/signin")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody SigninRequest signinRequest) {
		String jwt = authService.authenticateUser(signinRequest);
		log.info("this is jwt " +jwt);
		HttpHeaders responseHeader = new HttpHeaders();
		responseHeader.set("Authorization", "Bearer " + jwt);
		responseHeader.set("Access-Control-Expose-Headers", "Authorization");

		return ResponseEntity.ok().headers(responseHeader).build();
	}
}
