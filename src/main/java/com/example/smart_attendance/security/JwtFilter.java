package com.example.smart_attendance.security;


import jakarta.servlet.*;
import jakarta.servlet.http.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);
        String email = jwtUtil.extractEmail(token);

        if (email != null && jwtUtil.validateToken(token)) {

            var userDetails =
                    userDetailsService.loadUserByUsername(email);

            var authToken =
                    new UsernamePasswordAuthenticationToken(
                            userDetails, null,
                            userDetails.getAuthorities());

            SecurityContextHolder.getContext()
                    .setAuthentication(authToken);
        }

        filterChain.doFilter(request, response);
    }
}