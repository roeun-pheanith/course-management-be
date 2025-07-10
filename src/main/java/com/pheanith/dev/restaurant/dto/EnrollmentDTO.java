package com.pheanith.dev.restaurant.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class EnrollmentDTO {
	private Long userId;
	private Long courseId;
	private LocalDate enrollDate;
}
