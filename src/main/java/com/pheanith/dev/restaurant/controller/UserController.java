package com.pheanith.dev.restaurant.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pheanith.dev.restaurant.config.security.UserService;
import com.pheanith.dev.restaurant.dto.UserRolePermissionDTO;

@RestController
@RequestMapping("/api/user")
public class UserController {
	
	@Autowired
	private UserService userService;
	
	@GetMapping("/roles-permissions")
	public ResponseEntity<?> getRoleAndPermission(Principal principal){
		UserRolePermissionDTO userRolesAndPermissions = userService.getUserRolesAndPermissions(principal.getName());
		System.out.println("this is roles and permissions: %s ".formatted(userRolesAndPermissions));
		return ResponseEntity.ok(userRolesAndPermissions);
	}

}
