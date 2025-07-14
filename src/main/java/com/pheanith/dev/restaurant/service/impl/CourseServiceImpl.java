package com.pheanith.dev.restaurant.service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.pheanith.dev.restaurant.dto.CourseAccessStatusDTO;
import com.pheanith.dev.restaurant.dto.CourseRequestDTO;
import com.pheanith.dev.restaurant.dto.CourseResponseDTO;
import com.pheanith.dev.restaurant.entity.Course;
import com.pheanith.dev.restaurant.entity.User;
import com.pheanith.dev.restaurant.exception.ResourceNotFoundException;
import com.pheanith.dev.restaurant.mapper.CourseMapper;
import com.pheanith.dev.restaurant.repository.CourseRepository;
import com.pheanith.dev.restaurant.repository.UserRepository;
import com.pheanith.dev.restaurant.service.CourseService;
import com.pheanith.dev.restaurant.service.EnrollmentService;
import com.pheanith.dev.restaurant.service.util.PageUtil;
import com.pheanith.dev.restaurant.spec.CourseSpec;
import com.pheanith.dev.restaurant.spec.filter.CourseFilter;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;
    private final UserRepository userRepository;

    @Value("${image.upload.dir}")
    private String uploadDir;

    private String saveFile(MultipartFile file, String subDir) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }
        Path uploadPath = Paths.get(uploadDir, subDir).toAbsolutePath().normalize();
        Files.createDirectories(uploadPath);
        String originalFilename = file.getOriginalFilename();
        String fileExtension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String uniqueFileName = UUID.randomUUID().toString() + fileExtension;
        Path filePath = uploadPath.resolve(uniqueFileName);
        Files.copy(file.getInputStream(), filePath);
        return "/" + subDir + "/" + uniqueFileName;
    }

    private void deleteFile(String filePath) {
        if (filePath != null && !filePath.isEmpty()) {
            try {
                Path fileToDelete = Paths.get(uploadDir, filePath).toAbsolutePath().normalize();
                Files.deleteIfExists(fileToDelete);
            } catch (IOException e) {
                System.err.println("Failed to delete file: " + filePath + ", Error: " + e.getMessage());
            }
        }
    }

    @Override
    @Transactional
    public CourseResponseDTO createCourseWithFiles(CourseRequestDTO courseDTO, MultipartFile thumbnailFile, MultipartFile videoFile) throws IOException {
        Course course = courseMapper.toCourse(courseDTO);
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
        Course existingCourse = getById(id);
        if (thumbnailFile != null && !thumbnailFile.isEmpty()) {
            deleteFile(existingCourse.getThumbnailUrl());
            existingCourse.setThumbnailUrl(saveFile(thumbnailFile, "course/thumbnails"));
        }
        if (videoFile != null && !videoFile.isEmpty()) {
            deleteFile(existingCourse.getVideoUrl());
            existingCourse.setVideoUrl(saveFile(videoFile, "course/videos"));
        }
        courseMapper.updateCourseFromDto(courseDTO, existingCourse);
        Course updatedCourse = courseRepository.save(existingCourse);
        return courseMapper.toCourseResponseDTO(updatedCourse);
    }

    @Override
    @Transactional
    public void delete(Long courseId) {
        Course course = getById(courseId);
        deleteFile(course.getThumbnailUrl());
        deleteFile(course.getVideoUrl());
        courseRepository.delete(course);
    }

    @Override
    public Page<CourseResponseDTO> courseFilter(Map<String, String> params) {
        // ... (existing filter logic) ...
        CourseFilter courseFilter = new CourseFilter();
        if (params.containsKey("title")) {
            courseFilter.setTitle(params.get("title"));
        }
        if (params.containsKey("content")) {
            courseFilter.setContent(params.get("content"));
        }
        if (params.containsKey("description")) {
            courseFilter.setDescription(params.get("description"));
        }
        if (params.containsKey("isFree")) {
            courseFilter.setIsFree(Boolean.valueOf(params.get("isFree")));
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

        CourseSpec courseSpec = new CourseSpec(courseFilter);
        return courseRepository.findAll(courseSpec, pageable).map(course -> {
            CourseResponseDTO dto = courseMapper.toCourseResponseDTO(course);
            if (course.getThumbnailUrl() != null && !course.getThumbnailUrl().isEmpty()) {
                dto.setThumbnailUrl("/api/courses/" + course.getId() + "/thumbnail");
            } else {
                dto.setThumbnailUrl("");
            }
            if (course.getVideoUrl() != null && !course.getVideoUrl().isEmpty()) {
                 dto.setVideoUrl("/api/courses/" + course.getId() + "/video");
            } else {
                dto.setVideoUrl("");
            }
            return dto;
        });
    }

    @Override
    public List<CourseResponseDTO> getAllCourses() {
        return courseRepository.findAll().stream().map(courseMapper::toCourseResponseDTO)
                .map(dto -> {
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
    
    @Override
    public CourseAccessStatusDTO getPublicCourseAccessStatus(Long courseId) {
        return courseRepository.findById(courseId)
                .map(course -> new CourseAccessStatusDTO(course.getIsFree()))
                .orElse(new CourseAccessStatusDTO(false));
    }

}