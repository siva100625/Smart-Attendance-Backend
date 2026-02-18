package com.example.smart_attendance.repository;

import com.example.smart_attendance.model.Course;
import com.example.smart_attendance.model.Enrollment;
import com.example.smart_attendance.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    List<Enrollment> findByStudent(User student);

    List<Enrollment> findByCourse(Course course);

    boolean existsByStudentAndCourse(User student, Course course);
    @Modifying
    @Query("DELETE FROM Enrollment e WHERE e.student.id = :studentId")
    void deleteByStudentId(@Param("studentId") Long studentId);


    List<Enrollment> findByCourseId(Long id);


    void deleteByCourseId(Long id);
}
