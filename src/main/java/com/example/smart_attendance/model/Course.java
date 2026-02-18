package com.example.smart_attendance.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "courses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String courseName;

    private String courseCode;

    @ManyToOne
    @JoinColumn(name = "faculty_id")
    private User faculty; // can be null initially
}
