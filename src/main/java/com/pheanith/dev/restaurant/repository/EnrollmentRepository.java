package com.pheanith.dev.restaurant.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pheanith.dev.restaurant.entity.Enrollment;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long>{
	Optional<Enrollment> findByUserIdAndCourseId(Long userId, Long courseId);
	boolean existsByCourseIdAndUserId(Long courseId, Long userId);
}
