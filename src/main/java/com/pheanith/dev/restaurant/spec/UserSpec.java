package com.pheanith.dev.restaurant.spec;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.pheanith.dev.restaurant.entity.User;
import com.pheanith.dev.restaurant.spec.filter.UserFilter;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserSpec implements Specification<User>{

	private final UserFilter userFilter;
	private List<Predicate> predicates = new ArrayList<>();
	
	@Override
	public Predicate toPredicate(Root<User> user, CriteriaQuery<?> query, CriteriaBuilder cb) {
		if(userFilter.getUsername() != null) {
			Predicate username = cb.like(cb.lower(user.get("username")),"%" +  userFilter.getUsername().toLowerCase() + "%");
			predicates.add(username);
		}
		if(userFilter.getEmail() != null) {
			Predicate email = cb.like(cb.lower(user.get("email")), "%" + userFilter.getEmail().toLowerCase() + "%");
			predicates.add(email);
		}
		if(userFilter.getRole() != null) {
			Predicate role = cb.like(cb.lower(user.get("role")), "%" + userFilter.getRole().toLowerCase() + "%");
			predicates.add(role);
		}
		if(userFilter.getEnrolledCourse() != null) {
			cb.like(cb.lower(user.get("enrolledCourse")), "%" + userFilter.getEnrolledCourse().toLowerCase() + "%");
		}
		return cb.and(predicates.toArray(Predicate[]::new));
	}

}
