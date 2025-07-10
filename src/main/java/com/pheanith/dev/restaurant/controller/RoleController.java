package com.pheanith.dev.restaurant.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pheanith.dev.restaurant.dto.PermissionDTO;
import com.pheanith.dev.restaurant.dto.RoleDTO;
import com.pheanith.dev.restaurant.entity.Permission;
import com.pheanith.dev.restaurant.entity.Role;
import com.pheanith.dev.restaurant.mapper.PermissionMapper;
import com.pheanith.dev.restaurant.mapper.RoleMapper;
import com.pheanith.dev.restaurant.service.PermissionService;
import com.pheanith.dev.restaurant.service.RoleService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/auth/roles")
@Slf4j
@RequiredArgsConstructor
public class RoleController {

	private final RoleService roleService;

	private final RoleMapper roleMapper;

	private final PermissionService permissionService;

	private final PermissionMapper permissionMapper;

	@PostMapping
	public ResponseEntity<?> createRole(@RequestBody RoleDTO roleDTO) {
		log.info(roleDTO.getPermissionId().toString());
		return ResponseEntity.ok(roleService.create(roleMapper.toRole(roleDTO)));
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> getRoleById(@PathVariable Long roleId) {
		return ResponseEntity.ok(roleService.getById(roleId));
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> updateById(@PathVariable Long id, @RequestBody RoleDTO roleDTO) {

		Role updatedRole = roleService.update(roleMapper.toRole(roleDTO), id);

		return ResponseEntity.ok(updatedRole);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id) {
		roleService.delete(id);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/permissions")
	public ResponseEntity<?> createPermission(@RequestBody PermissionDTO permissionDTO) {
		Permission permission = permissionService.create(permissionMapper.toPermission(permissionDTO));
		return ResponseEntity.ok(permission);
	}

//	@GetMapping("permissions/{id}")
//	public ResponseEntity<?> getPermissionById(@PathVariable Long id) {
//		return ResponseEntity.ok(permissionService.getById(id));
//	}

	@PutMapping("permissions/{id}")
	public ResponseEntity<?> updatePermission(@PathVariable Long id, @RequestBody PermissionDTO permissionDTO) {
		Permission updatedPermission = permissionService.update(id, permissionMapper.toPermission(permissionDTO));
		return ResponseEntity.ok(updatedPermission);
	}
	
	@DeleteMapping("permissions/{id}")
	public ResponseEntity<?> deletePermission(@PathVariable Long id) {
		permissionService.delete(id);
		return ResponseEntity.ok().build();
	}
	
	@GetMapping
	public ResponseEntity<?> getRole(@RequestParam Map<String, String> params){
		List<Role> roleList = roleService.roleFilter(params);
		return ResponseEntity.ok(roleList);
	}
	
	@GetMapping("permissions/{id}")
	public ResponseEntity<?> getPermissionsByRoleId(@PathVariable Long id){
		return ResponseEntity.ok(roleService.getPermissionByRole(id));
	}
}
