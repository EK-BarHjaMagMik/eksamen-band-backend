package org.example.eksamenbandbackend.controller.admin;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.example.eksamenbandbackend.dto.PhotoResponse;
import org.example.eksamenbandbackend.dto.UpdatePhotoRequest;
import org.example.eksamenbandbackend.dto.UploadPhotosResponse;
import org.example.eksamenbandbackend.service.PhotoService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/photos")
public class AdminPhotoController {

    private final PhotoService photoService;

    public AdminPhotoController(PhotoService photoService) {
        this.photoService = photoService;
    }

    @PostMapping
    public ResponseEntity<UploadPhotosResponse> uploadPhotos(
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam(required = false) String caption,
            @RequestParam(required = false) String photographer,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTaken,
            @RequestParam(required = false) Long showId) {
        return new ResponseEntity<>(
                photoService.uploadPhotos(files, caption, photographer, dateTaken, showId),
                HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PhotoResponse> updatePhoto(@PathVariable Long id,
            @Valid @RequestBody UpdatePhotoRequest request) {
        return new ResponseEntity<>(photoService.updatePhoto(id, request), HttpStatus.OK);
    }
    
    @PatchMapping
    public ResponseEntity<List<PhotoResponse>> batchUpdatePhotos(@RequestBody Map<String, Object> payload) {
        // Basic validation: require non-empty photoIds list so controller returns 400 on invalid body
        if (payload == null || !payload.containsKey("photoIds") || !(payload.get("photoIds") instanceof List<?> raw)
                || raw.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "photoIds must be a non-empty list");
        }

        return new ResponseEntity<>(photoService.batchUpdatePhotos(payload), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePhoto(@PathVariable Long id) {
        boolean deleted = photoService.deletePhoto(id);

        if (!deleted) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }
}
