package com.pheanith.dev.restaurant.dto;

import java.util.Set;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponseDTO {
	private Long id;
	private String firstName;
	private String lastName;
	private String username;
	private String email;
	private Set<String> roles;
	private Set<String> enrolledCourses;
}
