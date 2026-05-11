package org.example.eksamenbandbackend.service;

import org.example.eksamenbandbackend.dto.CreateUserRequest;
import org.example.eksamenbandbackend.entity.User;
import org.example.eksamenbandbackend.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User createUser(CreateUserRequest request) {

        // Enforce username uniqueness before the password is encoded and stored.
        if (existsByUsername(request.username())) {
            throw new IllegalArgumentException("Username already exists");
        }

        // Email also needs to be unique for account recovery and login flows.
        if (existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email already exists");
        }

        User user = new User();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(request.role());
        // New accounts start enabled and unlocked unless an admin changes them later.
        user.setActive(true);
        user.setLocked(false);

        return userRepository.save(user);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
    }

    public boolean existsByUsername(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    public boolean existsByEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }
}
