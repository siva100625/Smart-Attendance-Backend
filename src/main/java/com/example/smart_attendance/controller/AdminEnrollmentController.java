package com.example.smart_attendance.controller;

import com.example.smart_attendance.model.Enrollment;
import com.example.smart_attendance.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/enrollments")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminEnrollmentController {

    private final EnrollmentService enrollmentService;

    @PostMapping("/{courseId}/assign-student/{studentId}")
    public Enrollment assignStudent(@PathVariable Long courseId,
                                    @PathVariable Long studentId) {
        return enrollmentService.assignStudent(studentId, courseId);
    }
}
