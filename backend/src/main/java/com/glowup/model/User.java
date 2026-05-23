package com.glowup.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank
    @Email
    @Column(unique = true, nullable = false)
    private String email;

    @NotBlank
    @Column(nullable = false)
    private String password;

    private String name;

    // Body measurements for virtual try-on mannequin (in cm)
    private Double bust;
    private Double waist;
    private Double hips;
    private Double height;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    // Default Constructor
    public User() {}

    public User(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.bust = 90.0;   // Default standard measurements
        this.waist = 70.0;
        this.hips = 95.0;
        this.height = 168.0;
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Double getBust() { return bust; }
    public void setBust(Double bust) { this.bust = bust; }

    public Double getWaist() { return waist; }
    public void setWaist(Double waist) { this.waist = waist; }

    public Double getHips() { return hips; }
    public void setHips(Double hips) { this.hips = hips; }

    public Double getHeight() { return height; }
    public void setHeight(Double height) { this.height = height; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
