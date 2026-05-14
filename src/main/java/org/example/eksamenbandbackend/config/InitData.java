package org.example.eksamenbandbackend.config;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.example.eksamenbandbackend.dto.CreateUserRequest;
import org.example.eksamenbandbackend.entity.BandBio;
import org.jspecify.annotations.NonNull;
import org.example.eksamenbandbackend.entity.ContactInfo;
import org.example.eksamenbandbackend.entity.Photo;
import org.example.eksamenbandbackend.repository.BandBioRepository;
import org.example.eksamenbandbackend.repository.PhotoRepository;
import org.example.eksamenbandbackend.entity.Show;
import org.example.eksamenbandbackend.repository.ContactInfoRepository;
import org.example.eksamenbandbackend.repository.ShowRepository;
import org.example.eksamenbandbackend.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class InitData implements CommandLineRunner {

    private final UserService userService;
    private final PhotoRepository photoRepository;
    private final ShowRepository showRepository;
    private final ContactInfoRepository contactInfoRepository;
    private final BandBioRepository bandBioRepository;

    public InitData(UserService userService, PhotoRepository photoRepository, ShowRepository showRepository, ContactInfoRepository contactInfoRepository, BandBioRepository bandBioRepository) {
        this.userService = userService;
        this.photoRepository = photoRepository;
        this.showRepository = showRepository;
        this.contactInfoRepository = contactInfoRepository;
        this.bandBioRepository = bandBioRepository;
    }

    @Override
    public void run(@NonNull String... args) {
        initAdmin();
        initSamplePhotos();
        initShows();
        initContactInfo();
        initBandBio();
    }

    private void initAdmin() {
        if (userService.existsByUsername("admin")) {
            System.out.println("User 'admin' already exists — skipping init.");
            return;
        }

        userService.createUser(new CreateUserRequest("admin", "admin@example.com", "admin", "ROLE_ADMIN"));
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

    private void initBandBio() {
        if (bandBioRepository.count() > 0) {
            System.out.println("Band bio already exists — skipping init.");
            return;
        }

        BandBio bio = new BandBio();
        bio.setContent("""
                STÜGG is a 5 piece heavy metal band from Denmark. The boys have been playing together since early 2017 and released their debut album "GO FOR THE THROAT" in 2020.

                They started working on their second album during the global pandemic, and the frustration of lockdowns worked as fuel for their music to go in a heavier direction.

                The result can be heard on their second album "Shepherd of The Pit" released June 2nd 2023.

                Their music can be described as heavy metal, with influences from Thrash and Groovemetal. They fuse thrashy and groovy riffs with melodic and catchy choruses, heavy breakdowns, fast guitar solos with an amalgamation of clean singing, screaming and growling. All in all a combination that surely will make your neck sore!""");

        bandBioRepository.save(bio);
        System.out.println("Band bio initialized");
    }

    private void initContactInfo() {
        ContactInfo contact = contactInfoRepository.findTopByOrderByIdAsc().orElse(new ContactInfo());

        boolean isNew = contact.getId() == null;
        boolean missingBookingEmail = contact.getBookingEmail() == null;
        boolean missingEmailNote = contact.getEmailNote() == null;
        boolean outdatedBookingNote = !"Management, bookings, media".equals(contact.getBookingNote());

        if (!isNew && !missingBookingEmail && !missingEmailNote && !outdatedBookingNote) {
            System.out.println("Contact info already up to date — skipping init.");
            return;
        }

        if (isNew) {
            contact.setEmail("stuggofficial@gmail.com");
        }
        contact.setBookingEmail("kinnie@beatbreaker.dk");
        contact.setEmailNote("Fan mail, questions, feedback");
        contact.setBookingNote("Management, bookings, media");

        contactInfoRepository.save(contact);
        System.out.println(isNew ? "Contact info initialized" : "Contact info updated with bookingEmail");
    }

}
