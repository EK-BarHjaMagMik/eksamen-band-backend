package org.example.eksamenbandbackend.dto;

public record UserResponse(
        Long id,
        String username,
        String email,
        String role,
        boolean active,
        boolean locked) {

    public static UserResponse fromEntity(org.example.eksamenbandbackend.entity.User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                user.isActive(),
                user.isLocked());
    }
}
