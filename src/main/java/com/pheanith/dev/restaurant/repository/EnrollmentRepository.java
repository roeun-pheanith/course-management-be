package com.pheanith.dev.restaurant.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pheanith.dev.restaurant.entity.Course;
import com.pheanith.dev.restaurant.entity.Enrollment;
import com.pheanith.dev.restaurant.entity.User;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long>{
	Optional<Enrollment> findByUserIdAndCourseId(Long userId, Long courseId);
}
