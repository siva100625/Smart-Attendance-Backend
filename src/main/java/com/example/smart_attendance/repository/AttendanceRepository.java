package com.example.smart_attendance.repository;

import com.example.smart_attendance.model.Attendance;
import com.example.smart_attendance.model.Course;
import com.example.smart_attendance.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    List<Attendance> findByStudentAndCourse(User student, Course course);

    long countByStudentAndCourse(User student, Course course);
    List<Attendance> findByCourseId(Long courseId);
    long countByStudentAndCourseAndPresentTrue(User student, Course course);

    boolean existsByStudentAndCourseAndDate(User student, Course course, LocalDate date);

    void deleteByCourseId(Long id);

    List<Attendance> findByStudent_IdAndCourse_Id(Long studentId, Long courseId);


    List<Attendance> findByStudent_Id(Long studentId);
}
