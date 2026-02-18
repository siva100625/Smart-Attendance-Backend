package com.example.smart_attendance.controller;

import com.example.smart_attendance.model.Course;
import com.example.smart_attendance.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/courses")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminCourseController {

    private final CourseService courseService;

    // Create course
    @PostMapping
    public Course createCourse(@RequestParam String name,
                               @RequestParam String code) {
        return courseService.createCourse(name, code);
    }

    // Assign faculty
    @PutMapping("/{courseId}/assign-faculty/{facultyId}")
    public Course assignFaculty(@PathVariable Long courseId,
                                @PathVariable Long facultyId) {
        return courseService.assignFaculty(courseId, facultyId);
    }
    @GetMapping
    public List<Course> getAllCourses() {
        return courseService.getAllCourses();
    }


}
