package com.pheanith.dev.restaurant.spec;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.pheanith.dev.restaurant.entity.Course;
import com.pheanith.dev.restaurant.spec.filter.CourseFilter;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.Data;

@Data
public class CourseSpec implements Specification<Course> {

	private final CourseFilter  courseFilter;
	List<Predicate> predicates = new ArrayList<>();

	@Override
	public Predicate toPredicate(Root<Course> course, CriteriaQuery<?> query, CriteriaBuilder cb) {

		if (courseFilter.getTitle() != null) {
			Predicate title = cb.like(cb.lower(course.get("title")), "%" + courseFilter.getTitle().toLowerCase() + "%");
			predicates.add(title);
		}
		
		if (courseFilter.getDescription() != null) {
			Predicate description = cb.like(cb.lower(course.get("description")), "%" + courseFilter.getDescription().toLowerCase() + "%");
			predicates.add(description);
		}
		
		if (courseFilter.getContent() != null) {
			Predicate content = cb.like(cb.lower(course.get("content")), "%" + courseFilter.getContent().toLowerCase() + "%");
			predicates.add(content);
		}
		
		if (courseFilter.getIsFree() != null) {
			Predicate free = course.get("isFree").in(courseFilter.getIsFree());
			predicates.add(free);
		}
		return cb.and(predicates.toArray(Predicate[]::new));
	}

}
