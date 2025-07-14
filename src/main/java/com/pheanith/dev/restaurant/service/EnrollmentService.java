package com.pheanith.dev.restaurant.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.pheanith.dev.restaurant.dto.EnrollmentDTO;
import com.pheanith.dev.restaurant.entity.Enrollment;

public interface EnrollmentService {

EnrollmentDTO enrollInCourse(Long userId, Long courseId);
    
    // This method is correctly structured for checking enrollment
    boolean hasUserEnrolledInCourse(Long courseId, Long userId);

    // Keep these if you need them for other features
    Optional<Enrollment> getByUserIdAndCourseId(Long userId, Long courseId);
    List<Enrollment> searchEnrollments(Map<String, String> params);
    Enrollment getById(Long id);
}
