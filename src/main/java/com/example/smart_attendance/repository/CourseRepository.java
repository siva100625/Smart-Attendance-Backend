package com.example.smart_attendance.repository;


import com.example.smart_attendance.model.Course;
import com.example.smart_attendance.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.List;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {


    List<Course> findByFaculty_Id(Long facultyId);


    Optional<Course> findByIdAndFacultyId(Long courseId, Long facultyId);

    void deleteByFacultyId(Long id);

    List<Course> findByFacultyId(Long id);

}
