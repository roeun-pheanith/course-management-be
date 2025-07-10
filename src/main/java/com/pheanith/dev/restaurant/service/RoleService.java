package com.pheanith.dev.restaurant.service;

import java.util.List;
import java.util.Map;

import com.pheanith.dev.restaurant.entity.Permission;
import com.pheanith.dev.restaurant.entity.Role;

public interface RoleService {
	Role create(Role role);
	Role getById(Long roleId);
	Role update(Role role, Long id);
	void delete(Long id);
	
	List<Permission> getPermissionByRole(Long roleId);
	
	List<Role> roleFilter(Map<String, String> params);
}
