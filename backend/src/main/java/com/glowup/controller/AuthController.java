package com.glowup.controller;

import com.glowup.model.User;
import com.glowup.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;

    public AuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Email is already registered."));
        }
        // Save user (in production, hash the password, here we use simple text for MVP)
        User savedUser = userRepository.save(user);
        return ResponseEntity.ok(savedUser);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String password = credentials.get("password");

        if (email == null || password == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Missing email or password."));
        }

        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty() || !userOpt.get().getPassword().equals(password)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Invalid email or password."));
        }

        return ResponseEntity.ok(userOpt.get());
    }

    @GetMapping("/profile/{userId}")
    public ResponseEntity<?> getProfile(@PathVariable UUID userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "User not found."));
        }
        return ResponseEntity.ok(userOpt.get());
    }

    @PutMapping("/profile/{userId}/measurements")
    public ResponseEntity<?> updateMeasurements(@PathVariable UUID userId, @RequestBody Map<String, Double> measurements) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "User not found."));
        }

        User user = userOpt.get();
        if (measurements.containsKey("bust")) user.setBust(measurements.get("bust"));
        if (measurements.containsKey("waist")) user.setWaist(measurements.get("waist"));
        if (measurements.containsKey("hips")) user.setHips(measurements.get("hips"));
        if (measurements.containsKey("height")) user.setHeight(measurements.get("height"));

        User updatedUser = userRepository.save(user);
        return ResponseEntity.ok(updatedUser);
    }
}
