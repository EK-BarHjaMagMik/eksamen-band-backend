package org.example.eksamenbandbackend.dto;

public record LoginRequest(
        String username,
        String password) {
}
