package com.pheanith.dev.restaurant.service.impl;

import com.pheanith.dev.restaurant.dto.CourseRequestDTO;
import com.pheanith.dev.restaurant.dto.CourseResponseDTO;
import com.pheanith.dev.restaurant.exception.ResourceNotFoundException;
import com.pheanith.dev.restaurant.mapper.CourseMapper;
import com.pheanith.dev.restaurant.entity.Course;
import com.pheanith.dev.restaurant.repository.CourseRepository;
import com.pheanith.dev.restaurant.service.CourseService;
import com.pheanith.dev.restaurant.spec.CourseSpec;
import com.pheanith.dev.restaurant.spec.filter.CourseFilter;
import com.pheanith.dev.restaurant.service.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor // Lombok to auto-inject final fields via constructor
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper; // Inject the MapStruct mapper

    @Value("${image.upload.dir}") // Make sure this matches your application.properties/yml
    private String uploadDir;

    // Helper to save files and return relative path
    private String saveFile(MultipartFile file, String subDir) throws IOException {
        if (file == null || file.isEmpty()) {
            return null; // Or throw an exception if file is mandatory
        }

        Path uploadPath = Paths.get(uploadDir, subDir).toAbsolutePath().normalize();
        Files.createDirectories(uploadPath); // Create directories if they don't exist

        String originalFilename = file.getOriginalFilename();
        String fileExtension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String uniqueFileName = UUID.randomUUID().toString() + fileExtension;
        Path filePath = uploadPath.resolve(uniqueFileName);
        Files.copy(file.getInputStream(), filePath);

        // Return relative path from base upload dir
        return "/" + subDir + "/" + uniqueFileName;
    }

    // Helper to delete files
    private void deleteFile(String filePath) {
        if (filePath != null && !filePath.isEmpty()) {
            try {
                Path fileToDelete = Paths.get(uploadDir, filePath).toAbsolutePath().normalize();
                Files.deleteIfExists(fileToDelete);
            } catch (IOException e) {
                System.err.println("Failed to delete file: " + filePath + ", Error: " + e.getMessage());
                // Log the error but don't rethrow, as it shouldn't stop the main operation
            }
        }
    }

    @Override
    @Transactional
    public CourseResponseDTO createCourseWithFiles(CourseRequestDTO courseDTO, MultipartFile thumbnailFile, MultipartFile videoFile) throws IOException {
        Course course = courseMapper.toCourse(courseDTO); // Map DTO to entity

        // Save files and set paths
        course.setThumbnailUrl(saveFile(thumbnailFile, "course/thumbnails"));
        course.setVideoUrl(saveFile(videoFile, "course/videos"));

        Course savedCourse = courseRepository.save(course);
        return courseMapper.toCourseResponseDTO(savedCourse);
    }

    @Override
    public Course getById(Long id) {
        return courseRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Course", id));
    }

    @Override
    @Transactional
    public CourseResponseDTO updateCourseWithFiles(Long id, CourseRequestDTO courseDTO, MultipartFile thumbnailFile, MultipartFile videoFile) throws IOException {
        Course existingCourse = getById(id); // Throws if not found

        // Delete old files if new ones are provided
        if (thumbnailFile != null && !thumbnailFile.isEmpty()) {
            deleteFile(existingCourse.getThumbnailUrl()); // Delete old thumbnail
            existingCourse.setThumbnailUrl(saveFile(thumbnailFile, "course/thumbnails")); // Save new
        }
        if (videoFile != null && !videoFile.isEmpty()) {
            deleteFile(existingCourse.getVideoUrl()); // Delete old video
            existingCourse.setVideoUrl(saveFile(videoFile, "course/videos")); // Save new
        }

        // Update other fields using MapStruct
        courseMapper.updateCourseFromDto(courseDTO, existingCourse);

        Course updatedCourse = courseRepository.save(existingCourse);
        return courseMapper.toCourseResponseDTO(updatedCourse);
    }

    @Override
    @Transactional
    public void delete(Long courseId) {
        Course course = getById(courseId); // Throws if not found

        // Delete associated files from storage
        deleteFile(course.getThumbnailUrl());
        deleteFile(course.getVideoUrl());

        courseRepository.delete(course);
    }

    @Override
    public Page<CourseResponseDTO> courseFilter(Map<String, String> params) {
        CourseFilter courseFilter = new CourseFilter();
        if (params.containsKey("title")) {
            courseFilter.setTitle(params.get("title"));
        }
        if (params.containsKey("content")) { // Filtering by the new 'content' field
            courseFilter.setContent(params.get("content"));
        }
        if (params.containsKey("description")) { // Keeping description filter if needed
            courseFilter.setDescription(params.get("description"));
        }
        if (params.containsKey("isFree")) {
            courseFilter.setIsFree(Boolean.valueOf(params.get("isFree")));
        }

        int pageLimit = PageUtil.DEFAULT_PAGE_LIMIT;
        if (params.containsKey(PageUtil.PAGE_LIMIT)) {
            pageLimit = Integer.parseInt(params.get(PageUtil.PAGE_LIMIT));
        }

        int pageNumber = PageUtil.DEFAULT_PAGE_NUMBER; // Renamed for clarity
        if (params.containsKey(PageUtil.PAGE_NUMBER)) {
            pageNumber = Integer.parseInt(params.get(PageUtil.PAGE_NUMBER));
        }
        Pageable pageable = PageUtil.getPageable(pageNumber, pageLimit); // Use getPageable

        CourseSpec courseSpec = new CourseSpec(courseFilter);
        return courseRepository.findAll(courseSpec, pageable).map(course -> {
            // Map entity to DTO using MapStruct and then set full URLs for frontend
            CourseResponseDTO dto = courseMapper.toCourseResponseDTO(course);
            if (course.getThumbnailUrl() != null && !course.getThumbnailUrl().isEmpty()) {
                dto.setThumbnailUrl("/api/courses/" + course.getId() + "/thumbnail"); // Dynamic URL for serving bytes
            } else {
                dto.setThumbnailUrl(""); // Or a default placeholder
            }
            if (course.getVideoUrl() != null && !course.getVideoUrl().isEmpty()) {
                 dto.setVideoUrl("/api/courses/" + course.getId() + "/video"); // Dynamic URL for serving bytes
            } else {
                dto.setVideoUrl(""); // Or a default placeholder
            }
            return dto;
        });
    }

    @Override
    public List<CourseResponseDTO> getAllCourses() {
        return courseRepository.findAll().stream().map(courseMapper::toCourseResponseDTO)
                .map(dto -> { // Post-process to add full URLs
                    if (dto.getThumbnailUrl() != null && !dto.getThumbnailUrl().isEmpty()) {
                        dto.setThumbnailUrl("/api/courses/" + dto.getId() + "/thumbnail");
                    } else {
                         dto.setThumbnailUrl("");
                    }
                    if (dto.getVideoUrl() != null && !dto.getVideoUrl().isEmpty()) {
                        dto.setVideoUrl("/api/courses/" + dto.getId() + "/video");
                    } else {
                        dto.setVideoUrl("");
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }
}