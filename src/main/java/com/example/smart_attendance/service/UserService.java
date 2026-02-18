package com.example.smart_attendance.service;

import com.example.smart_attendance.model.AuthProvider;
import com.example.smart_attendance.model.Course;
import com.example.smart_attendance.model.User;
import com.example.smart_attendance.model.Role;
import com.example.smart_attendance.repository.AttendanceRepository;
import com.example.smart_attendance.repository.CourseRepository;
import com.example.smart_attendance.repository.EnrollmentRepository;
import com.example.smart_attendance.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EnrollmentRepository  enrollmentRepository;
    private final CourseRepository courseRepository;
    private final AttendanceRepository attendanceRepository;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, EnrollmentRepository enrollmentRepository, CourseRepository courseRepository, AttendanceRepository attendanceRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.enrollmentRepository=enrollmentRepository;
        this.courseRepository = courseRepository;
        this.attendanceRepository = attendanceRepository;
    }

    // Get all users
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Get users by role
    public List<User> getAllUsersByRole(Role role) {
        return userRepository.findByRole(role);
    }

    // Create user with photo support
    public User createUser(User user, MultipartFile photo) {
        // Encode password
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Default provider
        if (user.getProvider() == null) {
            user.setProvider(AuthProvider.LOCAL);
        }

        // Save photo if provided
        try {
            if (photo != null && !photo.isEmpty()) {
                user.setPhoto(photo.getBytes());
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read photo file", e);
        }

        return userRepository.save(user);
    }


    // Update user
    public User updateUser(Long id, User userDetails) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setName(userDetails.getName());
        user.setEmail(userDetails.getEmail());
        user.setRole(userDetails.getRole());

        if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDetails.getPassword()));
        }

        if (userDetails.getPhoto() != null && userDetails.getPhoto().length > 0) {
            user.setPhoto(userDetails.getPhoto());
        }

        return userRepository.save(user);
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("User not found with id: " + id));
    }
    // Delete user
    @Transactional
    public void deleteUser(Long id) {

        // If faculty → delete all courses created by faculty
        List<Course> courses = courseRepository.findByFacultyId(id);

        for (Course course : courses) {
            attendanceRepository.deleteByCourseId(course.getId());  // 🔥 delete attendance first
            enrollmentRepository.deleteByCourseId(course.getId());  // then enrollments
        }

        courseRepository.deleteByFacultyId(id);  // then courses

        enrollmentRepository.deleteByStudentId(id); // if student

        userRepository.deleteById(id); // finally user
    }

}
