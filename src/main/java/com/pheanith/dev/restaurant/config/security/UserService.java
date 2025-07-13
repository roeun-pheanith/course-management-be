package com.pheanith.dev.restaurant.config.security;

import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;

import com.pheanith.dev.restaurant.dto.UserRolePermissionDTO;
import com.pheanith.dev.restaurant.entity.User;

public interface UserService {

	Optional<AuthUser> findUserByUsername(String username);
	UserRolePermissionDTO getUserRolesAndPermissions(String username);
	Page<?> userFilter(Map<String, String> params);
	User getUserById(Long id);
	void deleteUser(Long id);
}
