package com.pheanith.dev.restaurant.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.pheanith.dev.restaurant.dto.CourseAccessStatusDTO;
import com.pheanith.dev.restaurant.dto.CourseRequestDTO;
import com.pheanith.dev.restaurant.dto.CourseResponseDTO;
import com.pheanith.dev.restaurant.dto.PageDTO;
import com.pheanith.dev.restaurant.entity.Course;
import com.pheanith.dev.restaurant.exception.ResourceNotFoundException;
import com.pheanith.dev.restaurant.mapper.CourseMapper;
import com.pheanith.dev.restaurant.repository.CourseRepository;
import com.pheanith.dev.restaurant.service.AuthService;
import com.pheanith.dev.restaurant.service.CourseService;
import com.pheanith.dev.restaurant.service.EnrollmentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
@Slf4j
public class CourseController {

	private final AuthService authService;
	private final CourseService courseService;
	private final CourseRepository courseRepository;
	private final CourseMapper courseMapper;
	private final EnrollmentService enrollmentService;

	@Value("${image.upload.dir}")
	private String uploadDir;

	@PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<CourseResponseDTO> createCourse(@RequestPart("course") CourseRequestDTO courseDTO,
			@RequestPart("thumbnail") MultipartFile thumbnailFile, @RequestPart("video") MultipartFile videoFile) {
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
		return ResponseEntity.ok(dto);
	}

	@PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<CourseResponseDTO> updateCourse(@PathVariable Long id,
			@RequestPart("course") CourseRequestDTO courseDTO,
			@RequestPart(name = "thumbnail", required = false) MultipartFile thumbnailFile,
			@RequestPart(name = "video", required = false) MultipartFile videoFile) {
		try {
			CourseResponseDTO updatedCourse = courseService.updateCourseWithFiles(id, courseDTO, thumbnailFile,
					videoFile);
			return ResponseEntity.ok(updatedCourse);
		} catch (IOException e) {
			log.error("Failed to update course files", e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to update course files", e);
		} catch (ResourceNotFoundException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteCourse(@PathVariable Long id) {
		try {
			courseService.delete(id);
			return ResponseEntity.noContent().build();
		} catch (ResourceNotFoundException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
		}
	}

	@GetMapping("/filter")
	public ResponseEntity<PageDTO> getFilteredCourses(@RequestParam Map<String, String> params) {
		log.info("Filtering courses with params: {}", params);
		Page<CourseResponseDTO> coursePage = courseService.courseFilter(params);
		PageDTO pageDTO = new PageDTO(coursePage);
		log.info("PageDTO response: {}", pageDTO);
		return ResponseEntity.ok(pageDTO);
	}

	@GetMapping(value = "{id}/thumbnail", produces = { MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE,
			MediaType.IMAGE_GIF_VALUE })
	public ResponseEntity<byte[]> getThumbnailBytes(@PathVariable Long id) throws IOException {
		Course course = courseRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Course", id));
		if (course.getThumbnailUrl() == null || course.getThumbnailUrl().isEmpty()) {
			throw new ResourceNotFoundException("Thumbnail not found for course", id);
		}
		Path filePath = Paths.get(uploadDir, course.getThumbnailUrl()).toAbsolutePath().normalize();
		if (!Files.exists(filePath) || !Files.isReadable(filePath)) {
			throw new ResourceNotFoundException("Thumbnail file not found or unreadable", id);
		}
		byte[] imageBytes = Files.readAllBytes(filePath);
		String contentType = Files.probeContentType(filePath);
		return ResponseEntity.ok()
				.contentType(MediaType
						.parseMediaType(contentType != null ? contentType : MediaType.APPLICATION_OCTET_STREAM_VALUE))
				.body(imageBytes);
	}

	@GetMapping(value = "{id}/video", produces = { "video/*", MediaType.APPLICATION_OCTET_STREAM_VALUE })
	public ResponseEntity<byte[]> getVideoBytes(@PathVariable Long id) throws IOException {
		Course course = courseRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Course", id));
		if (course.getVideoUrl() == null || course.getVideoUrl().isEmpty()) {
			throw new ResourceNotFoundException("Video not found for course", id);
		}
		Path filePath = Paths.get(uploadDir, course.getVideoUrl()).toAbsolutePath().normalize();
		if (!Files.exists(filePath) || !Files.isReadable(filePath)) {
			throw new ResourceNotFoundException("Video file not found or unreadable", id);
		}
		byte[] videoBytes = Files.readAllBytes(filePath);
		String contentType = Files.probeContentType(filePath);
		return ResponseEntity.ok()
				.contentType(MediaType.parseMediaType(contentType != null ? contentType : "video/mp4"))
				.body(videoBytes);
	}

	// This is the new, correct endpoint for checking access status
	@GetMapping("/access/status/{courseId}")
    public ResponseEntity<CourseAccessStatusDTO> getCourseAccessStatus(
            @PathVariable Long courseId,
            Principal principal) {
        
        boolean hasAccess;
        Course course = courseService.getById(courseId); // Correctly gets the course
        
        if (principal == null) {
            hasAccess = course.getIsFree();
        } else {
            Long userId = authService.getUserIdByPrincipal(principal);
            boolean isEnrolled = enrollmentService.hasUserEnrolledInCourse(courseId, userId);
            hasAccess = course.getIsFree() || isEnrolled;
        }
        
        CourseAccessStatusDTO status = new CourseAccessStatusDTO();
        status.setHasAccess(hasAccess);
        
        return ResponseEntity.ok(status);
    }
	
	
}