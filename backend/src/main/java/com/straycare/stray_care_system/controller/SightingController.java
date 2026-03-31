package com.straycare.stray_care_system.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.straycare.stray_care_system.model.Sighting;
import com.straycare.stray_care_system.service.SightingService;

@RestController
@RequestMapping("/api/sightings")
public class SightingController {

    @Autowired
    private SightingService sightingService;

    @PostMapping
    public ResponseEntity<?> createSighting(@RequestBody Sighting sighting) {
        try {
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
}