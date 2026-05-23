package com.glowup.controller;

import com.glowup.model.ClothesItem;
import com.glowup.model.Outfit;
import com.glowup.repository.ClothesItemRepository;
import com.glowup.repository.OutfitRepository;
import com.glowup.service.StylingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/styling")
public class StylingController {

    private final StylingService stylingService;
    private final OutfitRepository outfitRepository;
    private final ClothesItemRepository clothesItemRepository;

    public StylingController(StylingService stylingService,
                             OutfitRepository outfitRepository,
                             ClothesItemRepository clothesItemRepository) {
        this.stylingService = stylingService;
        this.outfitRepository = outfitRepository;
        this.clothesItemRepository = clothesItemRepository;
    }

    /**
     * Gets daily weather-aware and color-harmonious styling recommendations for a user.
     */
    @GetMapping("/recommendations")
    public ResponseEntity<?> getRecommendations(@RequestParam("userId") UUID userId,
                                                @RequestParam(value = "city", required = false) String city) {
        try {
            List<StylingService.OutfitRecommendation> recommendations = 
                    stylingService.getDailyRecommendations(userId, city);
            return ResponseEntity.ok(recommendations);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error generating recommendations: " + e.getMessage()));
        }
    }

    /**
     * Saves a curated outfit combination to the database.
     */
    @PostMapping("/outfits")
    public ResponseEntity<?> saveOutfit(@RequestBody Map<String, Object> payload) {
        try {
            String userIdStr = (String) payload.get("userId");
            String name = (String) payload.get("name");
            String contextTag = (String) payload.get("contextTag");
            List<String> itemIdsList = (List<String>) payload.get("itemIds");

            if (userIdStr == null || name == null || itemIdsList == null || itemIdsList.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("message", "UserId, name, and non-empty itemIds list are required."));
            }

            UUID userId = UUID.fromString(userIdStr);
            List<ClothesItem> items = new ArrayList<>();
            for (String itemIdStr : itemIdsList) {
                Optional<ClothesItem> itemOpt = clothesItemRepository.findById(UUID.fromString(itemIdStr));
                if (itemOpt.isPresent()) {
                    items.add(itemOpt.get());
                } else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(Map.of("message", "Clothes item not found: " + itemIdStr));
                }
            }

            Outfit outfit = new Outfit(userId, name, contextTag, items);
            Outfit savedOutfit = outfitRepository.save(outfit);
            return ResponseEntity.ok(savedOutfit);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Failed to save outfit: " + e.getMessage()));
        }
    }

    /**
     * Retrieves saved outfits for a user.
     */
    @GetMapping("/outfits")
    public ResponseEntity<?> getSavedOutfits(@RequestParam("userId") UUID userId) {
        List<Outfit> outfits = outfitRepository.findByUserId(userId);
        return ResponseEntity.ok(outfits);
    }
}
