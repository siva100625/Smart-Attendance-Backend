package com.example.smart_attendance.service;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import com.example.smart_attendance.dto.StudentAttendanceReport;
import com.example.smart_attendance.model.*;
import com.example.smart_attendance.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final EmailService emailService;

    // FACULTY marks attendance
    public Attendance markAttendance(Long facultyId,
                                     Long studentId,
                                     Long courseId,
                                     boolean present) {

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        // ✅ Check faculty owns this course
        if (!course.getFaculty().getId().equals(facultyId)) {
            throw new RuntimeException("You are not assigned to this course");
        }

        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        // ✅ Check student enrolled
        if (!enrollmentRepository.existsByStudentAndCourse(student, course)) {
            throw new RuntimeException("Student not enrolled in this course");
        }

        LocalDate today = LocalDate.now();

        if (attendanceRepository.existsByStudentAndCourseAndDate(student, course, today)) {
            throw new RuntimeException("Attendance already marked today");
        }

        Attendance attendance = new Attendance();
        attendance.setStudent(student);
        attendance.setCourse(course);
        attendance.setDate(today);
        attendance.setPresent(present);

        return attendanceRepository.save(attendance);
    }

    // STUDENT view attendance percentage
    public double calculateAttendancePercentage(Long studentId, Long courseId) {

        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        long totalClasses = attendanceRepository.countByStudentAndCourse(student, course);
        long attended = attendanceRepository.countByStudentAndCourseAndPresentTrue(student, course);

        if (totalClasses == 0) return 0;

        return (double) attended / totalClasses * 100;
    }
    public List<StudentAttendanceReport> getCourseAttendanceReport(Long facultyId, Long courseId) {

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        // Ensure faculty owns this course
        if (!course.getFaculty().getId().equals(facultyId)) {
            throw new RuntimeException("You are not assigned to this course");
        }

        List<Attendance> attendanceList =
                attendanceRepository.findByCourseId(courseId);

        Map<Long, List<Attendance>> groupedByStudent =
                attendanceList.stream()
                        .collect(Collectors.groupingBy(a -> a.getStudent().getId()));

        List<StudentAttendanceReport> report = new ArrayList<>();

        for (Map.Entry<Long, List<Attendance>> entry : groupedByStudent.entrySet()) {

            List<Attendance> records = entry.getValue();
            User student = records.get(0).getStudent();

            long totalClasses = records.size();
            long presentCount = records.stream()
                    .filter(Attendance::isPresent)
                    .count();

            double percentage = (presentCount * 100.0) / totalClasses;

            String status = percentage >= 75 ? "GOOD" : "LOW";

            // 🔥 SEND EMAIL IF BELOW 75
            if (percentage < 75) {
                emailService.sendLowAttendanceMail(
                        student.getEmail(),
                        student.getName(),
                        percentage
                );
            }

            report.add(new StudentAttendanceReport(
                    student.getId(),
                    student.getName(),
                    totalClasses,
                    presentCount,
                    percentage,
                    status
            ));
        }


        return report;
    }

    public byte[] exportCourseReportToExcel(Long facultyId, Long courseId) {

        List<StudentAttendanceReport> report =
                getCourseAttendanceReport(facultyId, courseId);

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Attendance Report");

            // Header Row
            Row headerRow = sheet.createRow(0);
            String[] headers = {
                    "Student ID",
                    "Student Name",
                    "Total Classes",
                    "Present",
                    "Percentage",
                    "Status"
            };

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }

            // Data Rows
            int rowIdx = 1;
            for (StudentAttendanceReport dto : report) {
                Row row = sheet.createRow(rowIdx++);

                row.createCell(0).setCellValue(dto.getStudentId());
                row.createCell(1).setCellValue(dto.getStudentName());
                row.createCell(2).setCellValue(dto.getTotalClasses());
                row.createCell(3).setCellValue(dto.getPresent());
                row.createCell(4).setCellValue(dto.getPercentage());
                row.createCell(5).setCellValue(dto.getStatus());
            }

            workbook.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to export Excel file");
        }
    }
    public List<Attendance> getStudentAttendance(
            Long studentId,
            Long courseId
    ) {
        return attendanceRepository
                .findByStudent_IdAndCourse_Id(studentId, courseId);
    }


}
