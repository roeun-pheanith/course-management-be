package com.pheanith.dev.restaurant.mapper;

import org.mapstruct.Mapper;

import com.pheanith.dev.restaurant.dto.PermissionDTO;
import com.pheanith.dev.restaurant.entity.Permission;

@Mapper(componentModel = "spring")
public interface PermissionMapper {

	Permission toPermission(PermissionDTO permissionDTO);
}
