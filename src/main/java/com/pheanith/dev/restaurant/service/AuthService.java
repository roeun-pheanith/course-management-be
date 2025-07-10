package com.pheanith.dev.restaurant.service;

import java.util.Optional;

import com.pheanith.dev.restaurant.dto.SigninRequest;
import com.pheanith.dev.restaurant.dto.SignupRequest;
import com.pheanith.dev.restaurant.entity.User;

public interface AuthService {

	String createUser(SignupRequest signupRequest);
	String authenticateUser(SigninRequest signinRequest);
	User getByUsername(String name);
}
