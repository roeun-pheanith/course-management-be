package com.pheanith.dev.restaurant.service.impl;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pheanith.dev.restaurant.entity.Permission;
import com.pheanith.dev.restaurant.exception.ResourceNotFoundException;
import com.pheanith.dev.restaurant.repository.PermissionRepository;
import com.pheanith.dev.restaurant.service.PermissionService;

@Service
public class PermissionServiceImpl implements PermissionService {

	@Autowired
	private PermissionRepository permissionRepository;

	@Override
	public Set<Permission> getAllById(Set<Long> id) {
		return permissionRepository.findAllById(id).stream().collect(Collectors.toSet());
	}

	@Override
	public Permission create(Permission permission) {
		return permissionRepository.save(permission);
	}

	@Override
	public Permission getById(Long id) {

		return permissionRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Permission", id));
	}

	@Override
	public Permission update(Long id, Permission permission) {
		Permission permissionToUpdate = getById(id);
		permissionToUpdate.setName(permission.getName());
		return permissionRepository.save(permissionToUpdate);
	}

	@Override
	public void delete(Long id) {
		Permission permission = getById(id);
		permissionRepository.delete(permission);
	}
}
