package org.example.eksamenbandbackend.controller;

import org.example.eksamenbandbackend.dto.LoginRequest;
import org.example.eksamenbandbackend.dto.AuthResponse;
import org.example.eksamenbandbackend.entity.User;
import org.example.eksamenbandbackend.security.JwtUtil;
import org.example.eksamenbandbackend.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    public AuthController(AuthenticationManager authenticationManager,
            UserService userService,
            JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {

        try {
            // Let Spring Security verify the supplied credentials before issuing a token.
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.username(),
                            request.password()));
        } catch (AuthenticationException e) {
            // Keep the failure response generic so clients only know that login failed.
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Load the user record so the response can include the authenticated user's
        // role.
        User user = userService.findByUsername(request.username());
        // Generate the JWT only after authentication has succeeded.
        String token = jwtUtil.generateToken(user.getUsername());

        return ResponseEntity.ok(
                new AuthResponse(token, user.getUsername(), user.getRole()));
    }

}
