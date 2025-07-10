package com.pheanith.dev.restaurant.service;

import java.util.Set;

import com.pheanith.dev.restaurant.entity.Permission;

public interface PermissionService {
	Permission create(Permission permission);
	Set<Permission> getAllById(Set<Long> id);
	Permission getById(Long id);
	Permission update(Long id, Permission permission);
	void delete(Long id);
//	Set<Long> getBySetOfPermissionId(Set<Permission> permission);
	
}
