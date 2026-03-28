package com.example.smart_attendance.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.smart_attendance.repository.UserRepository;
import com.example.smart_attendance.model.*;
import com.example.smart_attendance.dto.*;
import com.example.smart_attendance.security.JwtUtil;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public void register(RegisterRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(
                passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.valueOf(request.getRole()));
        user.setProvider(AuthProvider.LOCAL);

        userRepository.save(user);
    }

    public AuthResponse login(LoginRequest request) {

        //  Authenticate user
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        //  Load user from database
        User user = userRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        //  Generate JWT
        String token = jwtUtil.generateToken(user.getEmail());

        //  Return token + role
        return new AuthResponse(token, user.getRole().name());
    }

}
