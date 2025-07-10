package com.pheanith.dev.restaurant.exception;

import org.springframework.http.HttpStatus;

import lombok.Data;

@Data
public class ErrorResponse {
	private final HttpStatus status;
	private final String message;
}
