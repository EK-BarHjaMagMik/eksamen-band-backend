package org.example.eksamenbandbackend.config;

import org.example.eksamenbandbackend.dto.CreateUserRequest;
import org.example.eksamenbandbackend.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class InitData implements CommandLineRunner {

    private final UserService userService;

    public InitData(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void run(String... args) throws Exception {
        initAdmin();
    }

    private void initAdmin() {
        boolean exists = userService.existsByUsername("admin");

        if (exists) {
            System.out.println("User 'admin' already exists — skipping init.");
            return;
        }

        CreateUserRequest admin = new CreateUserRequest(
                "admin",
                "admin@example.com",
                "admin",
                "ROLE_ADMIN");

        userService.createUser(admin);

        System.out.println("User 'admin' initialized");
    }

}
