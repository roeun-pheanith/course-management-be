package com.pheanith.dev.restaurant.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.pheanith.dev.restaurant.dto.CourseRequestDTO;
import com.pheanith.dev.restaurant.dto.CourseResponseDTO;
import com.pheanith.dev.restaurant.entity.Course;

@Mapper(componentModel = "spring") // Spring component model makes it an injectable Spring bean
public interface CourseMapper {

    // Removed INSTANCE field as it's not needed with componentModel = "spring"
    // CourseMapper INSTANCE = Mappers.getMapper(CourseMapper.class);

    // Map Course entity to CourseResponseDTO
    // Note: thumbnail/video URLs will be set in service layer for display logic
    CourseResponseDTO toCourseResponseDTO(Course course);

    // Map CourseRequestDTO to Course entity for creation
    // Ignore ID as it's auto-generated
    // Ignore thumbnailUrl and videoUrl as they are handled by file upload
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "thumbnailUrl", ignore = true)
    @Mapping(target = "videoUrl", ignore = true)
    @Mapping(target = "enrollment", ignore = true) // Ignore collections not part of DTO
    Course toCourse(CourseRequestDTO courseRequestDTO); // Renamed method for clarity

    // Update existing Course entity from CourseRequestDTO
    // Ignore ID, thumbnailUrl, videoUrl during update as they are not updated via DTO fields
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "thumbnailUrl", ignore = true)
    @Mapping(target = "videoUrl", ignore = true)
    @Mapping(target = "enrollment", ignore = true) // Ignore collections
    void updateCourseFromDto(CourseRequestDTO courseRequestDTO, @MappingTarget Course course);
}