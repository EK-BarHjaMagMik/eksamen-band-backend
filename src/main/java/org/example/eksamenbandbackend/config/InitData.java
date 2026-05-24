package org.example.eksamenbandbackend.config;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.example.eksamenbandbackend.dto.CreateUserRequest;
import org.example.eksamenbandbackend.dto.UploadPhotosResponse;
import org.example.eksamenbandbackend.entity.*;
import org.example.eksamenbandbackend.repository.BandBioRepository;
import org.example.eksamenbandbackend.repository.BandMemberRepository;
import org.example.eksamenbandbackend.repository.ContactInfoRepository;
import org.example.eksamenbandbackend.repository.PhotoRepository;
import org.example.eksamenbandbackend.repository.ShowRepository;
import org.example.eksamenbandbackend.service.PhotoService;
import org.example.eksamenbandbackend.service.UserService;
import org.jspecify.annotations.NullMarked;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.mock.web.MockMultipartFile;

@Component
@NullMarked
public class InitData implements CommandLineRunner {

    private final UserService userService;
    private final PhotoService photoService;

    private final PhotoRepository photoRepository;
    private final ShowRepository showRepository;
    private final ContactInfoRepository contactInfoRepository;
    private final BandBioRepository bandBioRepository;
    private final BandMemberRepository bandMemberRepository;

    public InitData(UserService userService, PhotoService photoService, PhotoRepository photoRepository,
            ShowRepository showRepository,
            ContactInfoRepository contactInfoRepository, BandBioRepository bandBioRepository,
            BandMemberRepository bandMemberRepository) {
        this.userService = userService;
        this.photoService = photoService;
        this.photoRepository = photoRepository;
        this.showRepository = showRepository;
        this.contactInfoRepository = contactInfoRepository;
        this.bandBioRepository = bandBioRepository;
        this.bandMemberRepository = bandMemberRepository;
    }

    @Override
    public void run(String... args) {
        initAdmin();
        initShows();
        initSamplePhotos();
        initContactInfo();
        initBandBio();
        initBandMember();
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

        Show s6 = new Show();
        s6.setDate(LocalDate.of(2024, 8, 17));
        s6.setCity("København NV");
        s6.setVenue("UHØRT");

        showRepository.saveAll(List.of(s1, s2, s3, s4, s5, s6));
        System.out.println("Shows initialized");
    }

    private void initSamplePhotos() {
        if (photoRepository.count() > 0) {
            System.out.println("Photos already exist — skipping sample photo init.");
            return;
        }

        List<MultipartFile> files = new ArrayList<>();

        try {
            for (int i = 1; i <= 10; i++) {
                files.add(loadSampleFile("photo" + i + ".jpg"));
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load sample photos", e);
        }

        UploadPhotosResponse response = photoService.uploadPhotos(
                files,
                "Sample caption",
                "Sample photographer",
                LocalDate.now());

        // Link last 5 photos to the UHØRT show
        var show = showRepository.findByVenue("UHØRT").orElse(null);

        response.uploaded().stream()
                .skip(5) // last 5
                .forEach(uploaded -> {
                    Photo p = photoRepository.findById(uploaded.id()).orElseThrow();
                    p.setShow(show);
                    photoRepository.save(p);
                });

        response.errors().forEach(err -> System.err.println(
                "Sample photo failed: " + err.filename() + " — " + err.reason()));
        System.out.println(response.uploaded().size() + " sample photos initialized");
    }

    private void initBandBio() {
        if (bandBioRepository.count() > 0) {
            System.out.println("Band bio already exists — skipping init.");
            return;
        }

        BandBio bio = new BandBio();
        bio.setContent(
                """
                        STÜGG is a 5 piece heavy metal band from Denmark. The boys have been playing together since early 2017 and released their debut album "GO FOR THE THROAT" in 2020.

                        They started working on their second album during the global pandemic, and the frustration of lockdowns worked as fuel for their music to go in a heavier direction.

                        The result can be heard on their second album "Shepherd of The Pit" released June 2nd 2023.

                        Their music can be described as heavy metal, with influences from Thrash and Groovemetal. They fuse thrashy and groovy riffs with melodic and catchy choruses, heavy breakdowns, fast guitar solos with an amalgamation of clean singing, screaming and growling. All in all a combination that surely will make your neck sore!""");

        bandBioRepository.save(bio);
        System.out.println("Band bio initialized");
    }

    private void initBandMember() {
        if (bandMemberRepository.count() > 0) {
            System.out.println("Band Members already exist — skipping init.");
            return;
        }

        BandMember m1 = new BandMember();
        m1.setName("Kasper");
        m1.setRole("Vocals");
        m1.setBio(
                "Kasper is the lead vocalist of STÜGG, known for his powerful and versatile voice that can switch between clean singing, screaming, and growling with ease.");
        m1.setDisplayOrder(1);

        BandMember m2 = new BandMember();
        m2.setName("Morten");
        m2.setRole("Guitar");
        m2.setBio(
                "Morten is the guitarist of STÜGG, responsible for crafting the band's heavy riffs and melodic solos that define their sound.");
        m2.setDisplayOrder(2);

        BandMember m3 = new BandMember();
        m3.setName("Andreas");
        m3.setRole("Guitar");
        m3.setBio(
                "Andreas is the second guitarist of STÜGG, complementing Morten with his own unique style and contributing to the band's dynamic guitar work.");
        m3.setDisplayOrder(3);

        BandMember m4 = new BandMember();
        m4.setName("Mikkel");
        m4.setRole("Bass");
        m4.setBio(
                "Mikkel is the bassist of STÜGG, providing the low-end foundation and groove that drives the band's heavy sound.");
        m4.setDisplayOrder(4);

        BandMember m5 = new BandMember();
        m5.setName("Gustav");
        m5.setRole("Drums");
        m5.setBio(
                "Gustav is the drummer of STÜGG, known for his powerful and precise drumming style that adds intensity and energy to the band's music.");
        m5.setDisplayOrder(5);
        bandMemberRepository.saveAll(List.of(m1, m2, m3, m4, m5));
        System.out.println("BandMembers initialized");

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

    private MultipartFile loadSampleFile(String filename) throws IOException {
        ClassPathResource resource = new ClassPathResource("sample-photos/" + filename);

        String contentType = switch (filename.substring(filename.lastIndexOf('.') + 1).toLowerCase()) {
            case "jpg", "jpeg" -> "image/jpeg";
            case "png" -> "image/png";
            case "webp" -> "image/webp";
            default -> "application/octet-stream";
        };

        return new MockMultipartFile(
                filename,
                filename,
                contentType,
                resource.getInputStream());
    }
}
