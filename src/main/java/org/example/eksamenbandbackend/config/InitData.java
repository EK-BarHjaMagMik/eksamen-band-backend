package org.example.eksamenbandbackend.config;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.example.eksamenbandbackend.dto.CreateUserRequest;
import org.example.eksamenbandbackend.entity.Photo;
import org.example.eksamenbandbackend.repository.PhotoRepository;
import org.example.eksamenbandbackend.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class InitData implements CommandLineRunner {

    private final UserService userService;
    private final PhotoRepository photoRepository;

    public InitData(UserService userService, PhotoRepository photoRepository) {
        this.userService = userService;
        this.photoRepository = photoRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        initAdmin();
        initSamplePhotos();
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

    private void initSamplePhotos() {
        if (photoRepository.count() > 0) {
            System.out.println("Photos already exist — skipping sample photo init.");
            return;
        }

        List<Photo> photos = new ArrayList<>();

        for (int i = 1; i <= 10; i++) {
            photos.add(createPhoto(
                    "/sample-photos/photo" + i + ".jpg",
                    "Sample caption " + i,
                    "Photographer " + i));
        }

        photoRepository.saveAll(photos);

        System.out.println("10 sample photos initialized");
    }

    private Photo createPhoto(String url, String caption, String photographer) {
        Photo p = new Photo();
        p.setUrl(url);
        p.setCaption(caption);
        p.setPhotographer(photographer);
        p.setDateTaken(LocalDate.now());
        return p;
    }
}
