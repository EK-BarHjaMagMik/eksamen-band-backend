package org.example.eksamenbandbackend.controller.admin;

import java.time.LocalDate;
import java.util.List;

import org.example.eksamenbandbackend.dto.UploadPhotosResponse;
import org.example.eksamenbandbackend.service.PhotoService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTaken) {
        return new ResponseEntity<>(
                photoService.uploadPhotos(files, caption, photographer, dateTaken),
                org.springframework.http.HttpStatus.OK);
    }
}
