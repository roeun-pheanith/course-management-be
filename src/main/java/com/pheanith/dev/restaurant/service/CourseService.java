package com.pheanith.dev.restaurant.service;

import com.pheanith.dev.restaurant.dto.CourseRequestDTO;
import com.pheanith.dev.restaurant.dto.CourseResponseDTO;
import com.pheanith.dev.restaurant.entity.Course;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface CourseService {
    // Renamed for better semantics (createWithFiles)
    CourseResponseDTO createCourseWithFiles(CourseRequestDTO courseDTO, MultipartFile thumbnailFile, MultipartFile videoFile) throws IOException;

    Course getById(Long id);

    // Renamed for better semantics (updateWithFiles)
    CourseResponseDTO updateCourseWithFiles(Long id, CourseRequestDTO courseDTO, MultipartFile thumbnailFile, MultipartFile videoFile) throws IOException;

    void delete(Long courseId);

    Page<CourseResponseDTO> courseFilter(Map<String, String> params);

    List<CourseResponseDTO> getAllCourses(); // If you still need this for a non-paginated list
}