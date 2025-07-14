package com.pheanith.dev.restaurant.dto;

import java.util.Set;

import lombok.Data;

@Data
public class UserRolePermissionDTO {
	private Long id;
	private String username;
	private Set<String> permissions;
	private Set<String> roles;
}
