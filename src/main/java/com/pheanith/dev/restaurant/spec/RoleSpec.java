package com.pheanith.dev.restaurant.spec;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.pheanith.dev.restaurant.entity.Role;
import com.pheanith.dev.restaurant.spec.filter.RoleFilter;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.Data;

@Data
public class RoleSpec implements Specification<Role> {

	private final RoleFilter roleFilter;
	List<Predicate> predicates = new ArrayList<>();

	@Override
	public Predicate toPredicate(Root<Role> role, CriteriaQuery<?> query, CriteriaBuilder cb) {
		if (roleFilter.getName() != null) {
			Predicate name = cb.like(cb.lower(role.get("name")), "%" + roleFilter.getName().toLowerCase() + "%");
			predicates.add(name);
		}
		if (roleFilter.getId() != null) {
			Predicate id = role.get("id").in(roleFilter.getId());
			predicates.add(id);
		}
		
		return cb.and(predicates.toArray(Predicate[]::new));
	}

}
