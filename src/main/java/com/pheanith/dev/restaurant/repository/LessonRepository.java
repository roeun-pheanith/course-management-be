package com.pheanith.dev.restaurant.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pheanith.dev.restaurant.entity.Course;
import com.pheanith.dev.restaurant.entity.Lesson;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long>{
	
}
