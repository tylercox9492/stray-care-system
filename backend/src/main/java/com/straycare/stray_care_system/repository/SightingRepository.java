package com.straycare.stray_care_system.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.straycare.stray_care_system.model.Sighting;

@Repository
public interface SightingRepository extends JpaRepository<Sighting, Long> {
    List<Sighting> findByDogId(Long dogId);
    List<Sighting> findByVerifiedTrueOrderByReportedAtDesc();
}
