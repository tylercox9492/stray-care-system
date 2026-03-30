package com.straycare.stray_care_system.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.straycare.stray_care_system.model.Dog;
import com.straycare.stray_care_system.repository.DogRepository;

@Service
public class DogService {
    
    @Autowired
    private DogRepository dogRepository;

    public List<Dog> getAllDogs() {
        return dogRepository.findAll();
    }

    public Optional<Dog> getDogById(Long id) {
        return dogRepository.findById(id);
    }

    public Dog saveDog(Dog dog) {
        return dogRepository.save(dog);
    }

    public Dog updateDog(Long id, Dog updatedDog) {
        return dogRepository.findById(id).map(dog -> {
            dog.setName(updatedDog.getName());
            dog.setBreed(updatedDog.getBreed());
            dog.setStatus(updatedDog.getStatus());
            dog.setPhotoUrl(updatedDog.getPhotoUrl());
            return dogRepository.save(dog);
        }).orElseThrow(() -> new RuntimeException("Dog not found with id " + id));
    }

    public void deleteDog(Long id) {
        dogRepository.deleteById(id);
    }
}
