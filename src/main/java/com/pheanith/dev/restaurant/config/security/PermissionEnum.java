package com.pheanith.dev.restaurant.config.security;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum PermissionEnum {
	USER_WRITE("user:write"),
	USER_READ("user:read"),
	COURSE_WRITE("course:write"),
	COURSE_READ("course:read");
	
	private String description;
	

}
