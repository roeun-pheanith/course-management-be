package com.pheanith.dev.restaurant.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import com.pheanith.dev.restaurant.dto.CourseAccessStatusDTO;
import com.pheanith.dev.restaurant.dto.CourseRequestDTO;
import com.pheanith.dev.restaurant.dto.CourseResponseDTO;
import com.pheanith.dev.restaurant.entity.Course;

public interface CourseService {
	CourseResponseDTO createCourseWithFiles(CourseRequestDTO courseDTO, MultipartFile thumbnailFile, MultipartFile videoFile) throws IOException;

    Course getById(Long id);

    CourseResponseDTO updateCourseWithFiles(Long id, CourseRequestDTO courseDTO, MultipartFile thumbnailFile, MultipartFile videoFile) throws IOException;

    void delete(Long courseId);

    Page<CourseResponseDTO> courseFilter(Map<String, String> params);

    List<CourseResponseDTO> getAllCourses();
    
    // This is the only method for public access status
    CourseAccessStatusDTO getPublicCourseAccessStatus(Long courseId);
}