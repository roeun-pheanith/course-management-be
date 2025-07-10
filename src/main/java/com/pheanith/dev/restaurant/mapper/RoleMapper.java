package com.pheanith.dev.restaurant.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.pheanith.dev.restaurant.dto.RoleDTO;
import com.pheanith.dev.restaurant.entity.Role;
import com.pheanith.dev.restaurant.service.PermissionService;

@Mapper(componentModel = "spring", uses = {PermissionService.class})
public interface RoleMapper {
	
	@Mapping(target = "permissions", source = "permissionId")
	Role toRole(RoleDTO roleDTO);

}
