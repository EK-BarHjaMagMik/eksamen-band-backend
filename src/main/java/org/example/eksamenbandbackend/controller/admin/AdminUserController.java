package org.example.eksamenbandbackend.controller.admin;

import jakarta.validation.Valid;
import org.example.eksamenbandbackend.dto.CreateUserRequest;
import org.example.eksamenbandbackend.dto.UserResponse;
import org.example.eksamenbandbackend.entity.User;
import org.example.eksamenbandbackend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    private final UserService userService;

    public AdminUserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        User user = userService.createUser(request);
        return ResponseEntity.ok(UserResponse.fromEntity(user));
    }

}
