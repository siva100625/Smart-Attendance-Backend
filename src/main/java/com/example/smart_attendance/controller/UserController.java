package com.example.smart_attendance.controller;

import com.example.smart_attendance.model.User;
import com.example.smart_attendance.model.Role;
import com.example.smart_attendance.service.UserService;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.ObjectMapper;


import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/admin")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Get all users
    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    // Get all students
    @GetMapping("/students")
    @PreAuthorize("hasRole('ADMIN')")
    public List<User> getAllStudents() {
        return userService.getAllUsersByRole(Role.STUDENT);
    }

    // Get all faculty
    @GetMapping("/faculty")
    @PreAuthorize("hasRole('ADMIN')")
    public List<User> getAllFaculty() {
        return userService.getAllUsersByRole(Role.FACULTY);
    }

    // Create user with optional photo

    @PostMapping(value = "/users", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public User createUser(
            @RequestPart("user") String userJson,
            @RequestPart(value = "photo", required = false) MultipartFile photo) throws IOException {

        // Convert JSON string to User object
        ObjectMapper mapper = new ObjectMapper();
        User user = mapper.readValue(userJson, User.class);

        // Delegate to service
        return userService.createUser(user, photo);
    }
    // Update user
    @PutMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public User updateUser(
            @PathVariable Long id,
            @RequestPart("user") String userJson,
            @RequestPart(value = "photo", required = false) MultipartFile photo) throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        User user = mapper.readValue(userJson, User.class);

        if (photo != null && !photo.isEmpty()) {
            user.setPhoto(photo.getBytes());
        }

        return userService.updateUser(id, user);
    }

    // Delete user
    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }

    // Optional: Get user photo
    @GetMapping("/users/{id}/photo")
    @PreAuthorize("hasRole('ADMIN')")
    public byte[] getUserPhoto(@PathVariable Long id) {
        User user = userService.getAllUsers().stream()
                .filter(u -> u.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("User not found"));

        return user.getPhoto();
    }

    @GetMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public User getUser(@PathVariable Long id) {
        return userService.getUserById(id);
    }

}
