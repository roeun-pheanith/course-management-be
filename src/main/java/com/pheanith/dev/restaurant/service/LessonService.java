package com.pheanith.dev.restaurant.service;

import java.util.List;
import java.util.Map;

import com.pheanith.dev.restaurant.entity.Lesson;

public interface LessonService {
	
	Lesson create(Lesson lesson);
	
	List<Lesson> getFilter(Map<String, String> params);
	
	List<Lesson> getByCourseId(Long id);
	

}
