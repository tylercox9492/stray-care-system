package com.straycare.stray_care_system.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.straycare.stray_care_system.model.Dog;
import com.straycare.stray_care_system.service.CloudinaryService;
import com.straycare.stray_care_system.service.DogService;
import com.straycare.stray_care_system.service.QRCodeService;

@RestController
@RequestMapping("/api/dogs")
public class DogController {

    @Autowired
    private DogService dogService;

    @Autowired
    private QRCodeService qrCodeService;

    @Autowired
    private CloudinaryService cloudinaryService;

    @GetMapping
    public ResponseEntity<List<Dog>> getAllDogs() {
        return ResponseEntity.ok(dogService.getAllDogs());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getDogById(@PathVariable Long id) {
        return dogService.getDogById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createDog(
            @RequestParam("name") String name,
            @RequestParam(value = "breed", required = false) String breed,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "photo", required = false) MultipartFile photo) {
        try {
            Dog dog = new Dog();
            dog.setName(name);
            dog.setBreed(breed);

            if (status != null && !status.isBlank()) {
                try {
                    dog.setStatus(Dog.DogStatus.valueOf(status.toLowerCase()));
                } catch (IllegalArgumentException e) {
                    return ResponseEntity.badRequest().body("Invalid status value: " + status);
                }
            }

            if (photo != null && !photo.isEmpty()) {
                CloudinaryService.UploadResult photoUpload = cloudinaryService.uploadFile(photo);
                dog.setPhotoUrl(photoUpload.url());
            }

            Dog savedDog = dogService.saveDog(dog);

            String qrCode = qrCodeService.generateQRCode(savedDog);
            savedDog.setQrCode(qrCode);

            dogService.saveDog(savedDog);

            return ResponseEntity.ok(savedDog);
        } catch (com.google.zxing.WriterException e) {
            return ResponseEntity.badRequest().body("Failed to generate QR code: " + e.getMessage());
        } catch (java.io.IOException e) {
            return ResponseEntity.badRequest().body("Failed to generate QR code image: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to create dog: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateDog(@PathVariable Long id, @org.springframework.web.bind.annotation.RequestBody Dog dog) {
        try {
            Dog updatedDog = dogService.updateDog(id, dog);
            return ResponseEntity.ok(updatedDog);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to update dog: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDog(@PathVariable Long id) {
        try {
            dogService.deleteDog(id);
            return ResponseEntity.ok("Dog deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to delete dog: " + e.getMessage());
        }
    }
    @GetMapping("/search")
    public ResponseEntity<List<Dog>> searchDogs(
    @RequestParam(required = false) String name,
    @RequestParam(required = false) String status) {
    return ResponseEntity.ok(dogService.searchDogs(name, status));
    }
}
