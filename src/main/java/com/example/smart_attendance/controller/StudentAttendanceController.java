package com.example.smart_attendance.controller;

import com.example.smart_attendance.model.Attendance;
import com.example.smart_attendance.model.Course;
import com.example.smart_attendance.model.User;
import com.example.smart_attendance.repository.UserRepository;
import com.example.smart_attendance.service.AttendanceService;
import com.example.smart_attendance.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/student/attendance")
@RequiredArgsConstructor
public class StudentAttendanceController {

    private final AttendanceService attendanceService;
    private final UserRepository userRepository;
    private final CourseService courseService;

    // ✅ 0️⃣ Get all courses for the logged-in student
    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/courses")
    public List<Course> getStudentCourses(@AuthenticationPrincipal UserDetails userDetails) {
        User student = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Student not found"));

        return courseService.getCoursesByStudentId(student.getId());
    }
    // ✅ 1️⃣ Get full attendance list for a specific course
    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/{courseId}")
    public List<Attendance> getStudentAttendance(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long courseId
    ) {
        User student = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Student not found"));

        return attendanceService.getStudentAttendance(student.getId(), courseId);
    }

    // ✅ 2️⃣ Get attendance percentage for a specific course
    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/{courseId}/percentage")
    public double getAttendancePercentage(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long courseId
    ) {
        User student = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Student not found"));

        return attendanceService.calculateAttendancePercentage(student.getId(), courseId);
    }
}