package com.pheanith.dev.restaurant.entity; // Use 'model' or 'entity' package

import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.ToString;

@Entity
@Table(name = "courses")
@Data 
@ToString(exclude = {"enrollment"}) 
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;

    @Column(columnDefinition = "TEXT") // Use TEXT type for potentially long content
    private String content;

    private String thumbnailUrl; // Relative path, e.g., /uploads/course/thumbnails/course_1_thumb.png
    private String videoUrl;     // Relative path, e.g., /uploads/course/videos/course_1_video.mp4

    private Boolean isFree;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Enrollment> enrollment; // Assuming this relationship is correct

}