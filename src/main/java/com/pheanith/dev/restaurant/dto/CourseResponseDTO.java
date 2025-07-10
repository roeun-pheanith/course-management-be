package com.pheanith.dev.restaurant.dto;

import lombok.Data;

@Data
public class CourseResponseDTO {

    private Long id;
    private String title;
    private String description;
    private String content; // Included as distinct field
    private Boolean isFree;
    private String thumbnailUrl; // URL path for thumbnail
    private String videoUrl;     // URL path for video
}