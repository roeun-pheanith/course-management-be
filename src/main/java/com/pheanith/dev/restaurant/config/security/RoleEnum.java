package com.pheanith.dev.restaurant.config.security;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import static com.pheanith.dev.restaurant.config.security.PermissionEnum.*;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum RoleEnum {

	ADMIN(Set.of(USER_WRITE, USER_READ, COURSE_WRITE, COURSE_READ)), USER(Set.of());

	private Set<PermissionEnum> permissions;

	public Set<SimpleGrantedAuthority> getAuthorities() {
		Set<SimpleGrantedAuthority> authority = this.permissions.stream()
				.map(permission -> new SimpleGrantedAuthority(permission.getDescription()))
				.collect(Collectors.toSet());
		
		SimpleGrantedAuthority roles = new SimpleGrantedAuthority("ROLE_" + this.name());
		authority.add(roles);
		return authority;
	}
}
