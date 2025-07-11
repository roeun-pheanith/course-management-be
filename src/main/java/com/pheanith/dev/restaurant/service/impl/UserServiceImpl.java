package com.pheanith.dev.restaurant.service.impl;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import com.pheanith.dev.restaurant.config.security.AuthUser;
import com.pheanith.dev.restaurant.config.security.UserService;
import com.pheanith.dev.restaurant.dto.UserRolePermissionDTO;
import com.pheanith.dev.restaurant.entity.Permission;
import com.pheanith.dev.restaurant.entity.Role;
import com.pheanith.dev.restaurant.entity.User;
import com.pheanith.dev.restaurant.exception.ApiException;
import com.pheanith.dev.restaurant.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;

	@Override
	public Optional<AuthUser> findUserByUsername(String username) {
		User user = findByUsername(username);
		AuthUser authUser = AuthUser.builder().username(user.getUsername()).password(user.getPassword())
				.authorities(getAuthorities(user.getRoles())).accountNonExpired(user.isAccountNonExpired())
				.accountNonLock(user.isAccountNonLock()).credentialNonExpired(user.isCredentialsNonExpired())
				.enable(user.isEnable()).build();

		return Optional.ofNullable(authUser);
	}

	private Set<SimpleGrantedAuthority> getAuthorities(Set<Role> roles) {
		Set<SimpleGrantedAuthority> authorities = roles.stream().flatMap(role -> {
			return role.getPermissions().stream().map(permission -> new SimpleGrantedAuthority(permission.getName()));
		}).collect(Collectors.toSet());
		Set<SimpleGrantedAuthority> authorityRole = roles.stream()
				.map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toSet());
		authorities.addAll(authorityRole);
		return authorities;
	}

	@Override
	public UserRolePermissionDTO getUserRolesAndPermissions(String username) {
		User user = findByUsername(username);

		Set<String> roles = new HashSet<>();
		Set<String> permissions = new HashSet<>();
		user.getRoles().forEach(role -> {
			String currentRole = role.getName();
			roles.add(currentRole);
			Set<String> permiss = role.getPermissions().stream().map(per -> {
				return per.getName();
			}).collect(Collectors.toSet());
			permissions.addAll(permiss);
		});

		UserRolePermissionDTO rolesPermissions = new UserRolePermissionDTO();
		rolesPermissions.setUsername(username);
		rolesPermissions.setRoles(roles);
		rolesPermissions.setPermissions(permissions);
		
		return rolesPermissions;
	}

	private User findByUsername(String username) {
		return userRepository.findByUsername(username)
				.orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "user not found!"));
	}

}
