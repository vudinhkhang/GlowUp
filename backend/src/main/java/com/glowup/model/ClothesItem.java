package com.glowup.model;

import com.glowup.util.JsonStringListConverter;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "clothes_items")
public class ClothesItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "item_id")
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private String category; // e.g. TOP, BOTTOM, OUTERWEAR, DRESS, SHOES

    @Column(name = "sub_category")
    private String subCategory; // e.g. T-Shirt, Jeans, Coat

    @Column(name = "primary_color")
    private String primaryColor; // e.g. #FFA500

    @Column(name = "hsl_hue")
    private Double hslHue; // Hue (0-360)

    @Column(name = "hsl_saturation")
    private Double hslSaturation; // Saturation (0-100)

    @Column(name = "hsl_lightness")
    private Double hslLightness; // Lightness (0-100)

    @Column(name = "image_url", nullable = false)
    private String imageUrl; // local upload URL

    @org.hibernate.annotations.JdbcTypeCode(org.hibernate.type.SqlTypes.JSON)
    @Column(name = "season_tags")
    private List<String> seasonTags = new ArrayList<>(); // e.g. ["Summer", "Spring"]

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public ClothesItem() {}

    public ClothesItem(UUID userId, String category, String subCategory, String primaryColor, 
                       Double hslHue, Double hslSaturation, Double hslLightness, String imageUrl, 
                       List<String> seasonTags) {
        this.userId = userId;
        this.category = category;
        this.subCategory = subCategory;
        this.primaryColor = primaryColor;
        this.hslHue = hslHue;
        this.hslSaturation = hslSaturation;
        this.hslLightness = hslLightness;
        this.imageUrl = imageUrl;
        this.seasonTags = seasonTags != null ? seasonTags : new ArrayList<>();
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getSubCategory() { return subCategory; }
    public void setSubCategory(String subCategory) { this.subCategory = subCategory; }

    public String getPrimaryColor() { return primaryColor; }
    public void setPrimaryColor(String primaryColor) { this.primaryColor = primaryColor; }

    public Double getHslHue() { return hslHue; }
    public void setHslHue(Double hslHue) { this.hslHue = hslHue; }

    public Double getHslSaturation() { return hslSaturation; }
    public void setHslSaturation(Double hslSaturation) { this.hslSaturation = hslSaturation; }

    public Double getHslLightness() { return hslLightness; }
    public void setHslLightness(Double hslLightness) { this.hslLightness = hslLightness; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public List<String> getSeasonTags() { return seasonTags; }
    public void setSeasonTags(List<String> seasonTags) { this.seasonTags = seasonTags; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
