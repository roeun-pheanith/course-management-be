package com.pheanith.dev.restaurant.dto;

import java.util.Set;

import lombok.Data;

@Data
public class RoleDTO {
	
	private String name;
	private Set<Long> permissionId;
}
