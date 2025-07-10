package com.pheanith.dev.restaurant.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.pheanith.dev.restaurant.entity.Enrollment;
import com.pheanith.dev.restaurant.exception.ApiException;
import com.pheanith.dev.restaurant.exception.ResourceNotFoundException;
import com.pheanith.dev.restaurant.repository.EnrollmentRepository;
import com.pheanith.dev.restaurant.service.EnrollmentService;

@Service
public class EnrollmentServiceImpl implements EnrollmentService {

	@Autowired
	private EnrollmentRepository enrollmentRepository;

	@Override
	public Enrollment enroll(Enrollment enrollment) {
		return enrollmentRepository.save(enrollment);
	}

	@Override
	public List<Enrollment> getEnroll(Map<String, String> params) {

		return null;
	}

	@Override
	public Enrollment getByUserIdAndCourseId(Long userId, Long courseId) {
		return enrollmentRepository.findByUserIdAndCourseId(userId, courseId)
				.orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "enrollment not found!"));

	}

}
