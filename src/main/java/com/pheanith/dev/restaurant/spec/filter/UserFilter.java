package com.pheanith.dev.restaurant.spec.filter;

import lombok.Data;

@Data
public class UserFilter {
	private String username;
	private String email;
	private String role;
	private String enrolledCourse;
}
