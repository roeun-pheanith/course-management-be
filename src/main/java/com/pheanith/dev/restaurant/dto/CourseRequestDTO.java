package com.pheanith.dev.restaurant.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CourseRequestDTO {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    @NotBlank(message = "Content is required")
    private String content; // Frontend sends this as a string

    @NotNull(message = "Is Free status is required")
    private Boolean isFree;

    // Files are handled separately as MultipartFile, so no need for them here.
}