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
@RequestMapping("/faculty/attendance")
@RequiredArgsConstructor
public class FacultyAttendanceController {
    private final CourseService courseService;
    private final AttendanceService attendanceService;
    private final UserRepository userRepository;
    @PreAuthorize("hasRole('FACULTY')")
    @PostMapping("/{courseId}/student/{studentId}")
    public Attendance markAttendance(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long courseId,
            @PathVariable Long studentId,
            @RequestParam boolean present) {

        // Get logged-in faculty email
        String email = userDetails.getUsername();

        // Fetch actual User entity from DB
        User faculty = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Faculty not found"));

        return attendanceService.markAttendance(
                faculty.getId(),
                studentId,
                courseId,
                present
        );
    }
    @PreAuthorize("hasRole('FACULTY')")
    @GetMapping("/courses")
    public List<Course> getFacultyCourses(
            @AuthenticationPrincipal UserDetails userDetails) {

        String email = userDetails.getUsername();

        User faculty = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Faculty not found"));

        return courseService.getCoursesByFacultyId(faculty.getId());
    }

    // ✅ Get students of selected course
    @PreAuthorize("hasRole('FACULTY')")
    @GetMapping("/course/{courseId}/students")
    public List<User> getStudentsOfCourse(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long courseId) {

        String email = userDetails.getUsername();

        User faculty = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Faculty not found"));

        return courseService.getStudentsByCourseAndFaculty(
                courseId,
                faculty.getId()
        );
    }


}
