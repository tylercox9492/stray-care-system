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

import com.straycare.stray_care_system.model.HealthRecord;
import com.straycare.stray_care_system.service.HealthRecordService;

@RestController
@RequestMapping("/api/health-records")
public class HealthRecordController {

    @Autowired
    private HealthRecordService healthRecordService;

    @PostMapping
    public ResponseEntity<?> createHealthRecord(@RequestBody HealthRecord healthRecord) {
        try {
            HealthRecord saved = healthRecordService.saveHealthRecord(healthRecord);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to save health record: " + e.getMessage());
        }
    }

    @GetMapping("/dog/{dogId}")
    public ResponseEntity<List<HealthRecord>> getHealthRecordsByDog(@PathVariable Long dogId) {
        return ResponseEntity.ok(healthRecordService.getHealthRecordsByDogId(dogId));
    }
}