package com.pheanith.dev.restaurant.entity; // Use 'model' or 'entity' package

import java.util.List;

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
@Data // Lombok for getters, setters, equals, hashCode, toString
@ToString(exclude = {"enrollment"}) // Exclude collections to prevent recursion in toString
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;

    @Column(columnDefinition = "TEXT") // Use TEXT type for potentially long content
    private String content;

    // Store file paths as String, not byte[]
    private String thumbnailUrl; // Relative path, e.g., /uploads/course/thumbnails/course_1_thumb.png
    private String videoUrl;     // Relative path, e.g., /uploads/course/videos/course_1_video.mp4

    private Boolean isFree;

    // Content types are not strictly needed in the DB if serving static files or inferring
    // private String thumbnailContentType; // Removed
    // private String videoContentType;     // Removed

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Enrollment> enrollment; // Assuming this relationship is correct

    // No need for @OneToMany with Lesson if Lesson has @ManyToOne to Course
    // Unless you want Course to manage Lesson lifecycle (e.g., cascade delete)
    // For simplicity, let's assume Lesson just references Course.
    // private List<Lesson> lessons; // If you manage lessons from Course side
}