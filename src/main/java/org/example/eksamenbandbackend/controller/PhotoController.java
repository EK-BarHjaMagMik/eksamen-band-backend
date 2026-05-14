package org.example.eksamenbandbackend.controller;

import java.util.List;

import org.example.eksamenbandbackend.dto.PhotoResponse;
import org.example.eksamenbandbackend.service.PhotoService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/photos")
public class PhotoController {

    private final PhotoService photoService;

    public PhotoController(PhotoService photoService) {
        this.photoService = photoService;
    }

    @GetMapping
    public ResponseEntity<List<PhotoResponse>> getPhotos() {
        return new ResponseEntity<>(photoService.getPhotos(), HttpStatus.OK);
    }
    
}
