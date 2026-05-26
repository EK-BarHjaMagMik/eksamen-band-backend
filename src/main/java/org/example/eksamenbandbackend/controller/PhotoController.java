package org.example.eksamenbandbackend.controller;

import java.util.List;

import org.example.eksamenbandbackend.dto.PhotoResponse;
import org.example.eksamenbandbackend.service.PhotoService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/photos")
public class PhotoController {

    private final PhotoService photoService;

    public PhotoController(PhotoService photoService) {
        this.photoService = photoService;
    }

    @GetMapping
    public ResponseEntity<List<PhotoResponse>> getPhotos(@RequestParam(required = false) Long showId) {
        if (showId != null) {
            return new ResponseEntity<>(photoService.getPhotosByShowId(showId), HttpStatus.OK);
        }
        return new ResponseEntity<>(photoService.getPhotos(), HttpStatus.OK);
    }

    @GetMapping("/recent")
    public ResponseEntity<List<PhotoResponse>> getRecentPhotos(@RequestParam(defaultValue = "6") int limit) {
        return new ResponseEntity<>(photoService.getRecentPhotos(limit), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PhotoResponse> getPhoto(@PathVariable Long id) {
        return new ResponseEntity<>(photoService.getPhoto(id), HttpStatus.OK);
    }

}
