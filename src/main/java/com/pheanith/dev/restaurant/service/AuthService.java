package com.pheanith.dev.restaurant.service;

import java.security.Principal;

import com.pheanith.dev.restaurant.dto.SigninRequest;
import com.pheanith.dev.restaurant.dto.SignupRequest;
import com.pheanith.dev.restaurant.entity.User;

public interface AuthService {

	String createUser(SignupRequest signupRequest);
	String authenticateUser(SigninRequest signinRequest);
	User getByUsername(String name);
	
	Long getUserIdByPrincipal(Principal principal);
}
