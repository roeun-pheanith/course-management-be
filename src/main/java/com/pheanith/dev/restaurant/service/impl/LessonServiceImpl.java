package com.pheanith.dev.restaurant.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pheanith.dev.restaurant.entity.Lesson;
import com.pheanith.dev.restaurant.repository.LessonRepository;
import com.pheanith.dev.restaurant.service.LessonService;

import jakarta.persistence.EntityManager;

@Service
public class LessonServiceImpl implements LessonService{
	
	@Autowired
	private LessonRepository lessonRepository;
	
	@Autowired
	private EntityManager entityManager;

	@Override
	public Lesson create(Lesson lesson) {
		return lessonRepository.save(lesson);
	}

	@Override
	public List<Lesson> getFilter(Map<String, String> params) {
		
		return null;
	}

	@Override
	public List<Lesson> getByCourseId(Long id) {
		String jpql = "SELECT l from Lesson WHERE l.course.id = :courseId";
		return entityManager.createQuery(jpql, Lesson.class)
				.setParameter("courseId", id)
				.getResultList();
	}
	
}
