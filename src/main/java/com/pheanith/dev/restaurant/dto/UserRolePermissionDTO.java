package com.pheanith.dev.restaurant.dto;

import java.util.Set;

import com.pheanith.dev.restaurant.entity.Permission;
import com.pheanith.dev.restaurant.entity.Role;

import lombok.Data;

@Data
public class UserRolePermissionDTO {
	private String username;
	private Set<Permission> permissions;
	private Set<Role> roles;
}
