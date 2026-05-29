package org.example.eksamenbandbackend.config;

import org.example.eksamenbandbackend.entity.Photo;
import org.example.eksamenbandbackend.repository.PhotoRepository;
import org.example.eksamenbandbackend.service.ImageProcessingService;
import org.jspecify.annotations.NullMarked;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * One-shot migration: photos uploaded before the EKS-124 WebP pipeline shipped
 * have null optimizedUrl / thumbnailUrl. On startup, find each such row,
 * generate the missing variants from the original file on disk, and persist
 * the URLs. Idempotent — re-running does nothing once all rows have non-null
 * URLs.
 * <p>
 * <b>Delete this entire class</b> after the first successful prod deploy logs
 * "Photo backfill complete: X succeeded, 0 skipped." It's pure dead weight
 * after that.
 */
@Component
@NullMarked
public class PhotoVariantBackfill implements CommandLineRunner {

    private final PhotoRepository photoRepository;
    private final ImageProcessingService imageProcessingService;
    private final String uploadDir;

    public PhotoVariantBackfill(PhotoRepository photoRepository,
                                ImageProcessingService imageProcessingService,
                                @Value("${app.upload-dir}") String uploadDir) {
        this.photoRepository = photoRepository;
        this.imageProcessingService = imageProcessingService;
        this.uploadDir = uploadDir;
    }

    @Override
    public void run(String... args) {
        List<Photo> needsBackfill = photoRepository.findAll().stream()
                .filter(p -> p.getOptimizedUrl() == null || p.getThumbnailUrl() == null)
                .toList();

        if (needsBackfill.isEmpty()) {
            System.out.println("All photos already have WebP variants — skipping backfill.");
            return;
        }

        System.out.println("Backfilling WebP variants for " + needsBackfill.size() + " photo(s)…");
        int success = 0;
        int skipped = 0;

        for (Photo photo : needsBackfill) {
            Path original = resolveUploadPath(photo.getUrl());
            if (original == null || !Files.exists(original)) {
                System.err.println("Skipping photo " + photo.getId()
                        + " — original file missing on disk: " + photo.getUrl());
                skipped++;
                continue;
            }
            try {
                if (photo.getOptimizedUrl() == null) {
                    Path opt = imageProcessingService.generateOptimized(original);
                    photo.setOptimizedUrl("/uploads/" + opt.getFileName());
                }
                if (photo.getThumbnailUrl() == null) {
                    Path thumb = imageProcessingService.generateThumbnail(original);
                    photo.setThumbnailUrl("/uploads/" + thumb.getFileName());
                }
                photoRepository.save(photo);
                success++;
            } catch (IOException | RuntimeException e) {
                System.err.println("Failed to backfill photo " + photo.getId()
                        + " (" + photo.getUrl() + "): " + e.getMessage());
                skipped++;
            }
        }

        System.out.println("Photo backfill complete: " + success + " succeeded, " + skipped + " skipped.");
    }

    private Path resolveUploadPath(String url) {
        if (url == null || !url.startsWith("/uploads/")) return null;
        String relative = url.substring("/uploads/".length());
        return Paths.get(uploadDir.trim()).resolve(relative);
    }
}
