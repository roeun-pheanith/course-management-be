package com.pheanith.dev.restaurant.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pheanith.dev.restaurant.entity.Permission;

public interface PermissionRepository extends JpaRepository<Permission, Long> {

}
