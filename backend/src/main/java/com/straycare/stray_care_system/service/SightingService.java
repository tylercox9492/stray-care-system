package com.straycare.stray_care_system.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.straycare.stray_care_system.model.Sighting;
import com.straycare.stray_care_system.repository.SightingRepository;

@Service
public class SightingService {
    @Autowired
    private SightingRepository sightingRepository;

    public Sighting saveSighting(Sighting sighting) {
        return sightingRepository.save(sighting);
    }

    public List<Sighting> getSightingsByDogId(Long dogId) {
        return sightingRepository.findByDogId(dogId);
    }
    public List<Sighting> getAllSightings() {
    return sightingRepository.findAll();
}
    public Sighting verifySighting(Long id) {
    return sightingRepository.findById(id).map(sighting -> {
        sighting.setVerified(true);
        return sightingRepository.save(sighting);
    }).orElseThrow(() -> new RuntimeException("Sighting not found with id " + id));
}
}