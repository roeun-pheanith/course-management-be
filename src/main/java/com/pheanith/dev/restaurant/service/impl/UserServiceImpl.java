package com.pheanith.dev.restaurant.service.impl;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import com.pheanith.dev.restaurant.config.security.AuthUser;
import com.pheanith.dev.restaurant.config.security.UserService;
import com.pheanith.dev.restaurant.dto.UserResponseDTO;
import com.pheanith.dev.restaurant.dto.UserRolePermissionDTO;
import com.pheanith.dev.restaurant.entity.Role;
import com.pheanith.dev.restaurant.entity.User;
import com.pheanith.dev.restaurant.exception.ApiException;
import com.pheanith.dev.restaurant.exception.ResourceNotFoundException;
import com.pheanith.dev.restaurant.repository.UserRepository;
import com.pheanith.dev.restaurant.service.util.PageUtil;
import com.pheanith.dev.restaurant.spec.UserSpec;
import com.pheanith.dev.restaurant.spec.filter.UserFilter;

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
		rolesPermissions.setId(user.getId());
		rolesPermissions.setUsername(username);
		rolesPermissions.setRoles(roles);
		rolesPermissions.setPermissions(permissions);

		return rolesPermissions;
	}

	private User findByUsername(String username) {
		return userRepository.findByUsername(username)
				.orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "user not found!"));
	}

	@Override
	public Page<UserResponseDTO> userFilter(Map<String, String> params) {
		UserFilter userFilter = new UserFilter();
		if (params.containsKey("username")) {
			userFilter.setUsername(params.get("username"));
		}
		if (params.containsKey("enrolledCourse")) {
			userFilter.setEnrolledCourse(params.get("enrolledCourse"));
		}
		if (params.containsKey("email")) {
			userFilter.setEmail(params.get("email"));
		}
		if (params.containsKey("role")) {
			userFilter.setRole(params.get("role"));
		}

		int pageLimit = PageUtil.DEFAULT_PAGE_LIMIT;
		if (params.containsKey(PageUtil.PAGE_LIMIT)) {
			pageLimit = Integer.parseInt(params.get(PageUtil.PAGE_LIMIT));
		}
		int pageNumber = PageUtil.DEFAULT_PAGE_NUMBER;
		if (params.containsKey(PageUtil.PAGE_NUMBER)) {
			pageNumber = Integer.parseInt(params.get(PageUtil.PAGE_NUMBER));
		}

		Pageable pageable = PageUtil.getPageable(pageNumber, pageLimit);
		UserSpec userSpec = new UserSpec(userFilter);
		return userRepository.findAll(userSpec, pageable).map(user -> {
			Set<String> enrolledCourse = user.getEnrolls().stream().map(enr -> {
				return enr.getCourse().getTitle();
			}).collect(Collectors.toSet());
			Set<String> roles = user.getRoles().stream().map(role -> {
				return role.getName();
			}).collect(Collectors.toSet());
			UserResponseDTO userResponseDTO = UserResponseDTO.builder().id(user.getId()).firstName(user.getFirstName())
					.lastName(user.getLastName()).username(user.getUsername()).email(user.getEmail()).roles(roles)
					.enrolledCourses(enrolledCourse).build();

			return userResponseDTO;
		});
	}

	@Override
	public User getUserById(Long id) {
		return userRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("User not found found with id: ", id));
	}

	@Override
	public void deleteUser(Long id) {
		userRepository.deleteById(id);
	}

}
