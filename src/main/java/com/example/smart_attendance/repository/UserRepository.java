package com.example.smart_attendance.repository;


import com.example.smart_attendance.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.smart_attendance.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    List<User> findByRole(Role role);
}