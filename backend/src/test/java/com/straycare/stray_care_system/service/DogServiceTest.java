package com.straycare.stray_care_system.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.straycare.stray_care_system.model.Dog;
import com.straycare.stray_care_system.repository.DogRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class DogServiceTest {

    @Mock
    private DogRepository dogRepository;

    @InjectMocks
    private DogService dogService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllDogs() {
        List<Dog> dogs = Arrays.asList(new Dog(), new Dog());

        when(dogRepository.findAll()).thenReturn(dogs);

        List<Dog> result = dogService.getAllDogs();

        assertEquals(2, result.size());
        verify(dogRepository).findAll();
    }

    @Test
    void testGetDogById() {
        Dog dog = new Dog();

        when(dogRepository.findById(1L)).thenReturn(Optional.of(dog));

        Optional<Dog> result = dogService.getDogById(1L);

        assertTrue(result.isPresent());
        verify(dogRepository).findById(1L);
    }

    @Test
    void testSaveDog() {
        Dog dog = new Dog();

        when(dogRepository.save(dog)).thenReturn(dog);

        Dog result = dogService.saveDog(dog);

        assertNotNull(result);
        verify(dogRepository).save(dog);
    }

    @Test
    void testDeleteDog() {
        dogService.deleteDog(1L);

        verify(dogRepository).deleteById(1L);
    }
}
