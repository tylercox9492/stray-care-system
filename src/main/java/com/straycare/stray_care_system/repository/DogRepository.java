package com.straycare.stray_care_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.straycare.stray_care_system.model.Dog;

@Repository
public interface DogRepository extends JpaRepository<Dog, Long> {
}