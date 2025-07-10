package com.pheanith.dev.restaurant.service.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pheanith.dev.restaurant.entity.Permission;
import com.pheanith.dev.restaurant.entity.Role;
import com.pheanith.dev.restaurant.exception.ResourceNotFoundException;
import com.pheanith.dev.restaurant.repository.PermissionRepository;
import com.pheanith.dev.restaurant.repository.RoleRepository;
import com.pheanith.dev.restaurant.service.RoleService;
import com.pheanith.dev.restaurant.spec.RoleSpec;
import com.pheanith.dev.restaurant.spec.filter.RoleFilter;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RoleServiceImpl implements RoleService {

	@Autowired
	private RoleRepository roleRepository;
	
	@Override
	public Role create(Role role) {
		return roleRepository.save(role);
	}

	@Override
	public Role getById(Long roleId) {
		return roleRepository.findById(roleId).orElseThrow(() -> new ResourceNotFoundException("Role", roleId));
	}

	@Override
	public void delete(Long id) {
		Role role = getById(id);
		roleRepository.deleteById(role.getId());
	}

	@Override
	public Role update(Role roleToUpdate, Long idToUpdate) {
		Role role = getById(idToUpdate);
		role.setName(roleToUpdate.getName());
		role.setPermissions(roleToUpdate.getPermissions());
		return roleRepository.save(role);
	}

	@Override
	public List<Role> roleFilter(Map<String, String> params) {
		RoleFilter roleFilter = new RoleFilter();
		if(params.containsKey("id")) {
			String id = params.get("id");
			roleFilter.setId(Long.parseLong(id));
		}
		
		if(params.containsKey("name")) {
			String name = params.get("name");
			roleFilter.setId(Long.parseLong(name));
		}
		RoleSpec roleSpec = new RoleSpec(roleFilter);
		return roleRepository.findAll(roleSpec);
	}

	@Override
	public List<Permission> getPermissionByRole(Long roleId) {
		Role role = getById(roleId);
		log.info(role.toString());
		List<Permission> list = role.getPermissions().stream().collect(Collectors.toList());
		return list;
	}
}
