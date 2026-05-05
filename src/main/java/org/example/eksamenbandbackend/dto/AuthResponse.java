package org.example.eksamenbandbackend.dto;

public record AuthResponse(
        String token,
        String username,
        String role) {
}
