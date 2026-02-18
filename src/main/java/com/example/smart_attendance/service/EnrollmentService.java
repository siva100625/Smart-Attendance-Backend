package com.example.smart_attendance.service;

import com.example.smart_attendance.model.Course;
import com.example.smart_attendance.model.Enrollment;
import com.example.smart_attendance.model.Role;
import com.example.smart_attendance.model.User;
import com.example.smart_attendance.repository.CourseRepository;
import com.example.smart_attendance.repository.EnrollmentRepository;
import com.example.smart_attendance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    public Enrollment assignStudent(Long studentId, Long courseId) {

        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        if (student.getRole() != Role.STUDENT) {
            throw new RuntimeException("User is not STUDENT");
        }

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        boolean exists = enrollmentRepository.existsByStudentAndCourse(student, course);
        if (exists) {
            throw new RuntimeException("Student already assigned to this course");
        }

        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setCourse(course);

        return enrollmentRepository.save(enrollment);
    }
}
