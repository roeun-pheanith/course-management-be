package com.pheanith.dev.restaurant.service;

import java.util.List;
import java.util.Map;

import com.pheanith.dev.restaurant.entity.Enrollment;

public interface EnrollmentService {

	Enrollment enroll(Enrollment enrollment);
	List<Enrollment> getEnroll(Map<String, String> params);
	Enrollment getByUserIdAndCourseId(Long userId, Long courseId);
}
