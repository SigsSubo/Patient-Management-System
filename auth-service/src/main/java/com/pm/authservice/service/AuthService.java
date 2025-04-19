package com.pm.authservice.service;

import com.pm.authservice.dto.LoginRequestDTO;
import com.pm.authservice.model.User;
import com.pm.authservice.util.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class AuthService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserService userService, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public Optional<String> authenticate(LoginRequestDTO loginRequestDTO) {

        // looks up user in db using email from Login Request DTO if it cant find user it returns null (as per Optional )
        // it them compares password from request with encoded password stored in db (returns null is password does not match)

        Optional<String> token = userService.findByEmail(loginRequestDTO.getEmail())
                // securely checks if passwords match without decoding the stored password.
                .filter(u -> passwordEncoder.matches(loginRequestDTO.getPassword(),
                        u.getPassword()))
                // If user exists and password matches, generates a JWT token containing the user's email and role.
                .map(u -> jwtUtil.generateToken(u.getEmail(), u.getRole()));

        return token;
    }

}
