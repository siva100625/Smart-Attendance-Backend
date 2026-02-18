package com.example.smart_attendance.service;

import com.example.smart_attendance.model.*;
import com.example.smart_attendance.repository.AttendanceRepository;
import com.example.smart_attendance.repository.CourseRepository;
import com.example.smart_attendance.repository.EnrollmentRepository;
import com.example.smart_attendance.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final AttendanceRepository attendanceRepository;

    // 1️⃣ Create Course
    public Course createCourse(String name, String code) {

        Course course = new Course();
        course.setCourseName(name);
        course.setCourseCode(code);

        return courseRepository.save(course);
    }

    // 2️⃣ Assign Faculty to Course
    public Course assignFaculty(Long courseId, Long facultyId) {

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        User faculty = userRepository.findById(facultyId)
                .orElseThrow(() -> new RuntimeException("Faculty not found"));

        if (faculty.getRole() != Role.FACULTY) {
            throw new RuntimeException("User is not FACULTY");
        }

        course.setFaculty(faculty);

        return courseRepository.save(course);
    }

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public List<Course> getCoursesByFacultyId(Long facultyId) {
        return courseRepository.findByFaculty_Id(facultyId);
    }

    // ✅ Get students enrolled in course (secure check)
    public List<User> getStudentsByCourseAndFaculty(Long courseId, Long facultyId) {

        // 🔐 Verify course belongs to this faculty
        Course course = courseRepository
                .findByIdAndFacultyId(courseId, facultyId)
                .orElseThrow(() ->
                        new RuntimeException("Course not found or not assigned to this faculty")
                );


        List<Enrollment> enrollments =
                enrollmentRepository.findByCourseId(course.getId());

        // Extract students
        return enrollments.stream()
                .map(Enrollment::getStudent)
                .toList();
    }
    public List<Course> getCoursesByStudentId(Long studentId) {
        return attendanceRepository.findByStudent_Id(studentId) // List<Attendance>
                .stream()
                .map(Attendance::getCourse) // strongly typed, no cast needed
                .distinct()
                .collect(Collectors.toList());
    }

}

