package org.example.eksamenbandbackend.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.example.eksamenbandbackend.dto.PhotoResponse;
import org.example.eksamenbandbackend.dto.UploadPhotosResponse;
import org.example.eksamenbandbackend.dto.UploadPhotosResponse.UploadError;
import org.example.eksamenbandbackend.dto.UploadPhotosResponse.UploadedPhoto;
import org.example.eksamenbandbackend.entity.Photo;
import org.example.eksamenbandbackend.repository.PhotoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class PhotoService {

    private static final Set<String> ALLOWED_TYPES = Set.of(
            "image/jpeg", "image/png", "image/webp");

    @Value("${app.upload-dir}")
    private String uploadDir;

    @Value("${app.max-upload-size-bytes:31457280}") // default 30MB
    private long maxUploadSizeBytes;

    private final PhotoRepository photoRepository;

    public PhotoService(PhotoRepository photoRepository) {
        this.photoRepository = photoRepository;
    }

    public List<PhotoResponse> getPhotos() {
        return photoRepository.findAllByOrderByDateTakenDesc().stream()
                .map(PhotoResponse::fromEntity)
                .toList();
    }

    public List<PhotoResponse> getRecentPhotos(int limit) {
        return photoRepository
                .findAllByOrderByDateTakenDesc(PageRequest.of(0, Math.max(1, limit)))
                .stream()
                .map(PhotoResponse::fromEntity)
                .toList();
    }

    public List<PhotoResponse> getPhotosByShowId(Long showId) {
        return photoRepository.findAllByShowIdOrderByDateTakenDesc(showId).stream()
                .map(PhotoResponse::fromEntity)
                .toList();
    }

    public UploadPhotosResponse uploadPhotos(
            List<MultipartFile> files,
            String caption,
            String photographer,
            LocalDate dateTaken) {

        List<UploadedPhoto> uploaded = new ArrayList<>();
        List<UploadError> errors = new ArrayList<>();

        for (MultipartFile file : files) {
            // Reject empty files
            if (file.isEmpty()) {
                errors.add(new UploadError(
                        file.getOriginalFilename(),
                        "File is empty"));
                continue;
            }

            // Enforce maximum file size
            if (file.getSize() > maxUploadSizeBytes) {
                errors.add(new UploadError(
                        file.getOriginalFilename(),
                        "File is too large: " + file.getSize() + " bytes"));
                continue;
            }

            // Validate file type
            if (!ALLOWED_TYPES.contains(file.getContentType())) {
                errors.add(new UploadError(
                        file.getOriginalFilename(),
                        "Unsupported file type: " + file.getContentType()));
                continue;
            }

            try {
                // save file to disk and get URL
                String url = saveFile(file);

                // Create DB entry
                Photo photo = new Photo();
                photo.setUrl(url);
                photo.setCaption(caption);
                photo.setPhotographer(photographer);
                photo.setDateTaken(dateTaken != null ? dateTaken : LocalDate.now());
                photoRepository.save(photo);

                uploaded.add(new UploadedPhoto(photo.getId(), url));

            } catch (IOException e) {
                errors.add(new UploadError(file.getOriginalFilename(), "Failed to save file"));
            }
        }

        return new UploadPhotosResponse(uploaded, errors);
    }

    public String saveFile(MultipartFile file) throws IOException {
        Path dir = Paths.get(uploadDir);
        Files.createDirectories(dir);

        String originalFilename = file.getOriginalFilename();
        String fileExt = "";

        // Safely extract extension
        if (originalFilename != null && originalFilename.contains(".")) {
            fileExt = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String filename = UUID.randomUUID() + fileExt;
        Path targetPath = dir.resolve(filename);

        try (InputStream in = file.getInputStream()) {
            Files.copy(in, targetPath);
        }

        return "/uploads/" + filename;
    }

}
