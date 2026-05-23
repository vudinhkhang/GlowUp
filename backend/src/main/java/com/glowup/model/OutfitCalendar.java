package com.glowup.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "outfit_calendar")
public class OutfitCalendar {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "calendar_id")
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private LocalDate date;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "outfit_id", nullable = false)
    private Outfit outfit;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public OutfitCalendar() {}

    public OutfitCalendar(UUID userId, LocalDate date, Outfit outfit) {
        this.userId = userId;
        this.date = date;
        this.outfit = outfit;
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public Outfit getOutfit() { return outfit; }
    public void setOutfit(Outfit outfit) { this.outfit = outfit; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
