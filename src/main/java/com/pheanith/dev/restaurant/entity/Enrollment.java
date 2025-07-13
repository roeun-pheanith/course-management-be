package com.pheanith.dev.restaurant.entity;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "enrollments")
public class Enrollment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // The relationship is with the User, who is the student.
    @ManyToOne
    @JoinColumn(name = "user_id") // Use a JoinColumn for clarity
    private User user;
    
    // This is correct
    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;
    
    // Add a grade field here
    private Double grade;
    
    private LocalDate enrollDate; // You can keep this if you want
}
