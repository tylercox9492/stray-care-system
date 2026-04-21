package com.straycare.stray_care_system.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.straycare.stray_care_system.model.Dog;
import com.straycare.stray_care_system.model.Sighting;
import com.straycare.stray_care_system.service.CloudinaryService;
import com.straycare.stray_care_system.service.DogService;
import com.straycare.stray_care_system.service.SightingService;

@RestController
@RequestMapping("/api/sightings")
public class SightingController {

    @Autowired
    private SightingService sightingService;

    @Autowired
    private DogService dogService;

    @Autowired
    private CloudinaryService cloudinaryService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createSighting(
            @RequestParam("dogId") Long dogId,
            @RequestParam("latitude") Double latitude,
            @RequestParam("longitude") Double longitude,
            @RequestParam(value = "notes", required = false) String notes,
            @RequestParam(value = "reportedBy", required = false) Long reportedBy,
            @RequestParam(value = "photo", required = false) MultipartFile photo) {
        try {
            Dog dog = dogService.getDogById(dogId)
                    .orElseThrow(() -> new RuntimeException("Dog not found"));

            Sighting sighting = new Sighting();
            sighting.setDog(dog);
            sighting.setLatitude(latitude);
            sighting.setLongitude(longitude);
            sighting.setNotes(notes);
            sighting.setReportedBy(reportedBy);

            if (photo != null && !photo.isEmpty()) {
                CloudinaryService.UploadResult photoUpload = cloudinaryService.uploadFile(photo);
                sighting.setPhotoUrl(photoUpload.url());
            }

            Sighting savedSighting = sightingService.saveSighting(sighting);
            return ResponseEntity.ok(savedSighting);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to submit sighting: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<Sighting>> getAllSightings() {
        return ResponseEntity.ok(sightingService.getAllSightings());
    }

    @GetMapping("/verified")
    public ResponseEntity<List<Sighting>> getVerifiedSightings() {
        return ResponseEntity.ok(sightingService.getVerifiedSightings());
    }

    @GetMapping("/dog/{dogId}")
    public ResponseEntity<List<Sighting>> getSightingsByDog(@PathVariable Long dogId) {
        return ResponseEntity.ok(sightingService.getSightingsByDogId(dogId));
    }

    @PutMapping("/{id}/verify")
    public ResponseEntity<?> verifySighting(@PathVariable Long id) {
        try {
            Sighting sighting = sightingService.verifySighting(id);
            return ResponseEntity.ok(sighting);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to verify sighting: " + e.getMessage());
        }
    }
}
