package com.straycare.stray_care_system.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.straycare.stray_care_system.config.JwtUtil;
import com.straycare.stray_care_system.controller.AuthController;
import com.straycare.stray_care_system.model.User;
import com.straycare.stray_care_system.service.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

class AuthControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testLoginSuccess() {

        User user = new User();
        user.setEmail("test@email.com");
        user.setPasswordHash("encodedPassword");
        user.setRole(User.UserRole.ADMIN);

        Map<String, String> loginData = new HashMap<>();
        loginData.put("email", "test@email.com");
        loginData.put("password", "plainPassword");

        when(userService.findByEmail("test@email.com"))
                .thenReturn(Optional.of(user));

        when(userService.checkPassword("plainPassword", "encodedPassword"))
                .thenReturn(true);

        when(jwtUtil.generateToken("test@email.com", "ADMIN"))
                .thenReturn("fake-jwt-token");

        ResponseEntity<?> response = authController.login(loginData);

        assertEquals(200, response.getStatusCodeValue());

        Map<?, ?> body = (Map<?, ?>) response.getBody();

        assertEquals("fake-jwt-token", body.get("token"));
        assertEquals("ADMIN", body.get("role"));
    }

    @Test
    void testLoginInvalidPassword() {

        User user = new User();
        user.setEmail("test@email.com");
        user.setPasswordHash("encodedPassword");
        user.setRole(User.UserRole.ADMIN);

        Map<String, String> loginData = new HashMap<>();
        loginData.put("email", "test@email.com");
        loginData.put("password", "wrongPassword");

        when(userService.findByEmail("test@email.com"))
                .thenReturn(Optional.of(user));

        when(userService.checkPassword("wrongPassword", "encodedPassword"))
                .thenReturn(false);

        ResponseEntity<?> response = authController.login(loginData);

        assertEquals(400, response.getStatusCodeValue());
    }
}