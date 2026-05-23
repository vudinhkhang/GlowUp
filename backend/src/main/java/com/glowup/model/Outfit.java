package com.glowup.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "outfits")
public class Outfit {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "outfit_id")
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    private String name;

    @Column(name = "context_tag")
    private String contextTag; // e.g. Work, Casual, Date, Party

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "outfit_items",
        joinColumns = @JoinColumn(name = "outfit_id"),
        inverseJoinColumns = @JoinColumn(name = "item_id")
    )
    private List<ClothesItem> items = new ArrayList<>();

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public Outfit() {}

    public Outfit(UUID userId, String name, String contextTag, List<ClothesItem> items) {
        this.userId = userId;
        this.name = name;
        this.contextTag = contextTag;
        this.items = items != null ? items : new ArrayList<>();
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getContextTag() { return contextTag; }
    public void setContextTag(String contextTag) { this.contextTag = contextTag; }

    public List<ClothesItem> getItems() { return items; }
    public void setItems(List<ClothesItem> items) { this.items = items; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
