package com.pheanith.dev.restaurant.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.pheanith.dev.restaurant.config.security.UserService;
import com.pheanith.dev.restaurant.dto.EnrollmentDTO;
import com.pheanith.dev.restaurant.entity.Course;
import com.pheanith.dev.restaurant.entity.Enrollment;
import com.pheanith.dev.restaurant.entity.User;
import com.pheanith.dev.restaurant.exception.ApiException;
import com.pheanith.dev.restaurant.repository.EnrollmentRepository;
import com.pheanith.dev.restaurant.service.CourseService;
import com.pheanith.dev.restaurant.service.EnrollmentService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EnrollmentServiceImpl implements EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final UserService userService;
    private final CourseService courseService;

    @Override
    public EnrollmentDTO enrollInCourse(Long userId, Long courseId) {
        // Step 1: Check if the user is already enrolled using your existing logic
        // FIXED: The correct parameter order is now used.
        if (enrollmentRepository.existsByCourseIdAndUserId(courseId, userId)) {
            throw new ApiException(HttpStatus.CONFLICT, "User is already enrolled in this course.");
        }
        
        // Step 2: Get the entities to establish the relationship
        User user = userService.getUserById(userId);
        Course course = courseService.getById(courseId);
        
        // Step 3: Create the new entity and save it
        Enrollment enrollment = new Enrollment(user, course);
        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);
        
        // Step 4: Convert and return a DTO
        EnrollmentDTO dto = new EnrollmentDTO();
        dto.setUserId(savedEnrollment.getUser().getId());
        dto.setCourseId(savedEnrollment.getCourse().getId());
        dto.setEnrollDate(savedEnrollment.getEnrollDate());
        dto.setGrade(savedEnrollment.getGrade());
        
        return dto;
    }

    @Override
    public Optional<Enrollment> getByUserIdAndCourseId(Long userId, Long courseId) {
        return enrollmentRepository.findByUserIdAndCourseId(userId, courseId);
    }
    
    @Override
    public List<Enrollment> searchEnrollments(Map<String, String> params) {
        return Collections.emptyList();
    }
    
    @Override
    public Enrollment getById(Long id) {
        return enrollmentRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Enrollment not found with id: " + id));
    }
    
    @Override
    public boolean hasUserEnrolledInCourse(Long courseId, Long userId) {
        return enrollmentRepository.existsByCourseIdAndUserId(courseId, userId);
    }
}