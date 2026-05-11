package org.example.eksamenbandbackend.config;

import org.example.eksamenbandbackend.dto.CreateUserRequest;
import org.example.eksamenbandbackend.entity.Show;
import org.example.eksamenbandbackend.repository.ShowRepository;
import org.example.eksamenbandbackend.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class InitData implements CommandLineRunner {

    private final UserService userService;
    private final ShowRepository showRepository;

    public InitData(UserService userService, ShowRepository showRepository) {
        this.userService = userService;
        this.showRepository = showRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        initAdmin();
        initShows();
    }

    private void initAdmin() {
        if (userService.existsByUsername("admin")) {
            System.out.println("User 'admin' already exists — skipping init.");
            return;
        }

        userService.createUser(new CreateUserRequest("admin", "admin@example.com", "admin", "ROLE_ADMIN"));
        System.out.println("User 'admin' initialized");
    }

    private void initShows() {
        if (showRepository.count() > 0) {
            System.out.println("Shows already exist — skipping init.");
            return;
        }

        Show s1 = new Show();
        s1.setDate(LocalDate.of(2026, 3, 27));
        s1.setCity("Helsingør");
        s1.setVenue("Elværket");

        Show s2 = new Show();
        s2.setDate(LocalDate.of(2026, 4, 2));
        s2.setCity("Albertslund");
        s2.setVenue("CTMF");

        Show s3 = new Show();
        s3.setDate(LocalDate.of(2026, 4, 10));
        s3.setCity("Lyngby");
        s3.setVenue("Demant Salen, DTU");

        Show s4 = new Show();
        s4.setDate(LocalDate.of(2026, 6, 4));
        s4.setCity("TBA");
        s4.setVenue("TBA");

        Show s5 = new Show();
        s5.setDate(LocalDate.of(2026, 9, 25));
        s5.setCity("Køge");
        s5.setVenue("Tapperiet");
        s5.setTicketLink("https://www.tapperiet.nu");

        showRepository.saveAll(List.of(s1, s2, s3, s4, s5));
        System.out.println("Shows initialized");
    }

}
