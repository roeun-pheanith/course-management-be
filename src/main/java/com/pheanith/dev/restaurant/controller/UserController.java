package com.pheanith.dev.restaurant.controller;

import java.security.Principal;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pheanith.dev.restaurant.config.security.UserService;
import com.pheanith.dev.restaurant.dto.PageDTO;
import com.pheanith.dev.restaurant.dto.UserRolePermissionDTO;

@RestController
@RequestMapping("/api/user")
public class UserController {
	
	@Autowired
	private UserService userService;
	
	@GetMapping
	public ResponseEntity<?> getUserByPage(Map<String, String> params){
		Page<?> userFilter = userService.userFilter(params);
		PageDTO pageDTO = new PageDTO(userFilter);
		return ResponseEntity.ok(pageDTO);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<?> getUserById(@PathVariable Long id){
		return ResponseEntity.ok(userService.getUserById(id));
	}
	
	@GetMapping("/roles-permissions")
	public ResponseEntity<?> getRoleAndPermission(Principal principal){
		UserRolePermissionDTO userRolesAndPermissions = userService.getUserRolesAndPermissions(principal.getName());
		System.out.println("this is roles and permissions: %s ".formatted(userRolesAndPermissions));
		return ResponseEntity.ok(userRolesAndPermissions);
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteUser(@PathVariable Long id) {
		userService.deleteUser(id);
		return ResponseEntity.ok().build();
	}
	
	

}
