package com.straycare.stray_care_system.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import com.straycare.stray_care_system.model.User;
import com.straycare.stray_care_system.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterUser() {
        User user = new User();
        user.setPasswordHash("plainPassword");

        when(passwordEncoder.encode("plainPassword")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        User savedUser = userService.registerUser(user);

        assertNotNull(savedUser);
        verify(passwordEncoder).encode("plainPassword");
        verify(userRepository).save(user);
    }

    @Test
    void testFindByEmail() {
        User user = new User();
        user.setEmail("test@email.com");

        when(userRepository.findByEmail("test@email.com")).thenReturn(Optional.of(user));

        Optional<User> result = userService.findByEmail("test@email.com");

        assertTrue(result.isPresent());
        assertEquals("test@email.com", result.get().getEmail());
    }

    @Test
    void testCheckPassword() {
        when(passwordEncoder.matches("plain", "encoded")).thenReturn(true);

        boolean result = userService.checkPassword("plain", "encoded");

        assertTrue(result);
        verify(passwordEncoder).matches("plain", "encoded");
    }
}
