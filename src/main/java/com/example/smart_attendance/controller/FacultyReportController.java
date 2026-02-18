package com.example.smart_attendance.controller;

import com.example.smart_attendance.dto.StudentAttendanceReport;
import com.example.smart_attendance.model.User;
import com.example.smart_attendance.repository.UserRepository;
import com.example.smart_attendance.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/faculty/course")
@RequiredArgsConstructor
public class FacultyReportController {

    private final AttendanceService attendanceService;
    private final UserRepository userRepository;

    @PreAuthorize("hasRole('FACULTY')")
    @GetMapping("/{courseId}/report")
    public List<StudentAttendanceReport> getReport(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long courseId) {

        String email = userDetails.getUsername();

        User faculty = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Faculty not found"));

        return attendanceService.getCourseAttendanceReport(
                faculty.getId(),
                courseId
        );
    }
    @GetMapping("/{courseId}/export")
    @PreAuthorize("hasRole('FACULTY')")
    public ResponseEntity<byte[]> exportExcel(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long courseId) {

        String email = userDetails.getUsername();

        User faculty = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Faculty not found"));

        byte[] excelData =
                attendanceService.exportCourseReportToExcel(
                        faculty.getId(),
                        courseId
                );

        return ResponseEntity.ok()
                .header("Content-Disposition",
                        "attachment; filename=attendance_report.xlsx")
                .header("Content-Type",
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                .body(excelData);
    }

}