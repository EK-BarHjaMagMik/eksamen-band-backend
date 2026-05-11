package org.example.eksamenbandbackend.dto;

public record CreateUserRequest(
        String username,
        String email,
        String password,
        String role) {
}
