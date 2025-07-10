package com.pheanith.dev.restaurant.config.security;

import java.util.Optional;

import com.pheanith.dev.restaurant.dto.UserRolePermissionDTO;

public interface UserService {

	Optional<AuthUser> findUserByUsername(String username);
	UserRolePermissionDTO getUserRolesAndPermissions(String username);
}
