package com.pheanith.dev.restaurant.controller;

import com.pheanith.dev.restaurant.dto.CourseRequestDTO;
import com.pheanith.dev.restaurant.dto.CourseResponseDTO;
import com.pheanith.dev.restaurant.dto.PageDTO;
import com.pheanith.dev.restaurant.exception.ResourceNotFoundException;
import com.pheanith.dev.restaurant.mapper.CourseMapper;
import com.pheanith.dev.restaurant.entity.Course;
import com.pheanith.dev.restaurant.entity.Enrollment; // Assuming correct path
import com.pheanith.dev.restaurant.entity.User; // Assuming correct path
import com.pheanith.dev.restaurant.repository.CourseRepository;
import com.pheanith.dev.restaurant.service.AuthService;
import com.pheanith.dev.restaurant.service.CourseService;
import com.pheanith.dev.restaurant.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor // Lombok to auto-inject final fields via constructor
@Slf4j
public class CourseController {

    private final AuthService authService;
    private final CourseService courseService;
    private final EnrollmentService enrollmentService;
    private final CourseRepository courseRepository; // Keep for direct access for file serving
    private final CourseMapper courseMapper; // Inject MapStruct mapper
    
    @Value("${image.upload.dir}") // <-- HERE IT IS!
    private String uploadDir;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CourseResponseDTO> createCourse(
            @RequestPart("course") CourseRequestDTO courseDTO,
            @RequestPart("thumbnail") MultipartFile thumbnailFile,
            @RequestPart("video") MultipartFile videoFile) {
        try {
            CourseResponseDTO createdCourse = courseService.createCourseWithFiles(courseDTO, thumbnailFile, videoFile);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdCourse);
        } catch (IOException e) {
            log.error("Failed to upload course files", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to upload course files", e);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseResponseDTO> getCourseById(@PathVariable Long id) {
        Course course = courseService.getById(id);
        // Map to DTO and set full URLs for display
        CourseResponseDTO dto = courseMapper.toCourseResponseDTO(course);
        if (course.getThumbnailUrl() != null && !course.getThumbnailUrl().isEmpty()) {
            dto.setThumbnailUrl("/api/courses/" + course.getId() + "/thumbnail");
        } else {
            dto.setThumbnailUrl(""); // Or a default placeholder
        }
        if (course.getVideoUrl() != null && !course.getVideoUrl().isEmpty()) {
            dto.setVideoUrl("/api/courses/" + course.getId() + "/video");
        } else {
            dto.setVideoUrl(""); // Or a default placeholder
        }
        return ResponseEntity.ok(dto);
    }

    // UPDATE endpoint: now handles files and uses CourseRequestDTO
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CourseResponseDTO> updateCourse(
            @PathVariable Long id,
            @RequestPart("course") CourseRequestDTO courseDTO,
            @RequestPart(name = "thumbnail", required = false) MultipartFile thumbnailFile, // Files are optional for update
            @RequestPart(name = "video", required = false) MultipartFile videoFile) {
        try {
            CourseResponseDTO updatedCourse = courseService.updateCourseWithFiles(id, courseDTO, thumbnailFile, videoFile);
            return ResponseEntity.ok(updatedCourse);
        } catch (IOException e) {
            log.error("Failed to update course files", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to update course files", e);
        } catch (ResourceNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @DeleteMapping("/{id}") // Corrected to use @PathVariable
    public ResponseEntity<Void> deleteCourse(@PathVariable Long id) {
        try {
            courseService.delete(id);
            return ResponseEntity.noContent().build(); // 204 No Content for successful deletion
        } catch (ResourceNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/filter") // Use /filter for the filtered list
    public ResponseEntity<PageDTO> getFilteredCourses(@RequestParam Map<String, String> params) {
        log.info("Filtering courses with params: {}", params);
        Page<CourseResponseDTO> coursePage = courseService.courseFilter(params);
        PageDTO pageDTO = new PageDTO(coursePage);
        log.info("PageDTO response: {}", pageDTO);
        return ResponseEntity.ok(pageDTO);
    }

    // Endpoint for serving thumbnail bytes directly (if not serving static files via web server)
    @GetMapping(value = "{id}/thumbnail", produces = {MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_GIF_VALUE})
    public ResponseEntity<byte[]> getThumbnailBytes(@PathVariable Long id) throws IOException {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course", id));
        // Read file bytes from disk instead of DB
        if (course.getThumbnailUrl() == null || course.getThumbnailUrl().isEmpty()) {
            throw new ResourceNotFoundException("Thumbnail not found for course", id);
        }
        Path filePath = Paths.get(uploadDir, course.getThumbnailUrl()).toAbsolutePath().normalize();
        if (!Files.exists(filePath) || !Files.isReadable(filePath)) {
            throw new ResourceNotFoundException("Thumbnail file not found or unreadable", id);
        }
        byte[] imageBytes = Files.readAllBytes(filePath);
        String contentType = Files.probeContentType(filePath); // Determine content type dynamically
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType != null ? contentType : MediaType.APPLICATION_OCTET_STREAM_VALUE))
                .body(imageBytes);
    }

    // Endpoint for serving video bytes directly (if not serving static files via web server)
    @GetMapping(value = "{id}/video", produces = {"video/*", MediaType.APPLICATION_OCTET_STREAM_VALUE}) // Broader video types
    public ResponseEntity<byte[]> getVideoBytes(@PathVariable Long id) throws IOException {
        Course course = courseRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Course", id));
        // Read file bytes from disk instead of DB
        if (course.getVideoUrl() == null || course.getVideoUrl().isEmpty()) {
            throw new ResourceNotFoundException("Video not found for course", id);
        }
        Path filePath = Paths.get(uploadDir, course.getVideoUrl()).toAbsolutePath().normalize();
        if (!Files.exists(filePath) || !Files.isReadable(filePath)) {
            throw new ResourceNotFoundException("Video file not found or unreadable", id);
        }
        byte[] videoBytes = Files.readAllBytes(filePath);
        String contentType = Files.probeContentType(filePath); // Determine content type dynamically
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType != null ? contentType : "video/mp4")) // Default to video/mp4 if unknown
                .body(videoBytes);
    }

    // Endpoint for getting course with user enrollment status
    @GetMapping("/{courseId}/lessons") // This name is confusing, it should be about Course not lessons
    public ResponseEntity<Course> getCourseWithEnrollmentStatus(@PathVariable Long courseId, Principal principal) {
        if (principal == null || principal.getName() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated.");
        }
        User user = authService.getByUsername(principal.getName());
        Enrollment enroll = enrollmentService.getByUserIdAndCourseId(user.getId(), courseId);
        // This assumes getByUserIdAndCourseId returns an Enrollment object that *contains* the Course.
        // If it directly returns the Course, then the DTO mapping should be applied here.
        if (enroll == null) {
            // Handle case where user is not enrolled, if course is not free
            // You might return a CourseResponseDTO without sensitive lesson info, or redirect to enrollment page
            Course course = courseService.getById(courseId); // Get the course anyway
            // You might return a limited DTO for non-enrolled users, or throw 403 if it's a paid course
            return ResponseEntity.ok(course); // Returning raw Course, consider a DTO
        }
        return ResponseEntity.ok(enroll.getCourse()); // Return the course from the enrollment
    }
}