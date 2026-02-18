package com.example.smart_attendance.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StudentAttendanceReport {

    private Long studentId;
    private String studentName;
    private long totalClasses;
    private long present;
    private double percentage;
    private String status;
}
