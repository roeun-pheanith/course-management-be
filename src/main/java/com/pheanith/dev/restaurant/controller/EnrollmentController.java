package com.pheanith.dev.restaurant.controller;

import java.security.Principal;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pheanith.dev.restaurant.dto.EnrollmentDTO;
import com.pheanith.dev.restaurant.service.AuthService;
import com.pheanith.dev.restaurant.service.EnrollmentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;
    private final AuthService authService;

    @PostMapping
//    @PreAuthorize("hasRole('USER')") // Only a USER can enroll
    public ResponseEntity<EnrollmentDTO> enrollUser(@RequestBody EnrollmentDTO enrollmentDTO, Principal principal) {
        // Get the user ID from the authenticated user's token (Principal)
        Long userId = authService.getUserIdByPrincipal(principal);
        
        // Call the service with the secure user ID and the course ID from the DTO
        EnrollmentDTO newEnrollment = enrollmentService.enrollInCourse(userId, enrollmentDTO.getCourseId());
        
        return ResponseEntity.status(HttpStatus.CREATED).body(newEnrollment);
    }
}