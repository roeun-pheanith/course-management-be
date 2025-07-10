package com.pheanith.dev.restaurant.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping
@RestController
public class TestController {
	@PostMapping("/test")
	public ResponseEntity<?> code(@RequestBody String username) {
		return ResponseEntity.ok("this is test for " + username);
	}
}
