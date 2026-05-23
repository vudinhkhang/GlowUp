package com.glowup.controller;

import com.glowup.dto.ScanResult;
import com.glowup.model.ClothesItem;
import com.glowup.repository.ClothesItemRepository;
import com.glowup.service.AIServiceClient;
import com.glowup.service.ColorHarmonyService;
import com.glowup.service.StorageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/closet")
public class ClosetController {

    private final ClothesItemRepository clothesItemRepository;
    private final AIServiceClient aiServiceClient;
    private final StorageService storageService;
    private final ColorHarmonyService colorHarmonyService;

    public ClosetController(ClothesItemRepository clothesItemRepository,
                            AIServiceClient aiServiceClient,
                            StorageService storageService,
                            ColorHarmonyService colorHarmonyService) {
        this.clothesItemRepository = clothesItemRepository;
        this.aiServiceClient = aiServiceClient;
        this.storageService = storageService;
        this.colorHarmonyService = colorHarmonyService;
    }

    /**
     * Uploads an image, contacts the AI scanning microservice to remove the background
     * and find the primary color, saves the result locally, and returns the scan metadata.
     */
    @PostMapping("/scan")
    public ResponseEntity<?> scanGarment(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "File upload is required."));
        }

        try {
            // Call FastAPI Microservice to remove background and extract dominant color
            ScanResult scanResult = aiServiceClient.scanGarment(file);

            // Store background-removed PNG file on local disk
            String imageUrl = storageService.storeBytes(scanResult.imageBytes(), file.getOriginalFilename());

            // Convert Hex to HSL color coordinates
            double[] hsl = colorHarmonyService.hexToHsl(scanResult.primaryColor());

            // Return scan results to client
            return ResponseEntity.ok(Map.of(
                    "imageUrl", imageUrl,
                    "primaryColor", scanResult.primaryColor(),
                    "hslHue", hsl[0],
                    "hslSaturation", hsl[1],
                    "hslLightness", hsl[2]
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Failed to process image: " + e.getMessage()));
        }
    }

    /**
     * Adds an item to the closet database.
     */
    @PostMapping("/items")
    public ResponseEntity<?> addClothesItem(@RequestBody ClothesItem item) {
        if (item.getUserId() == null || item.getCategory() == null || item.getImageUrl() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "UserId, category, and imageUrl are required."));
        }

        // Fill HSL if not already present
        if (item.getHslHue() == null && item.getPrimaryColor() != null) {
            double[] hsl = colorHarmonyService.hexToHsl(item.getPrimaryColor());
            item.setHslHue(hsl[0]);
            item.setHslSaturation(hsl[1]);
            item.setHslLightness(hsl[2]);
        }

        ClothesItem savedItem = clothesItemRepository.save(item);
        return ResponseEntity.ok(savedItem);
    }

    /**
     * Lists closet items for a user, with optional category filtering.
     */
    @GetMapping("/items")
    public ResponseEntity<?> getClothesItems(@RequestParam("userId") UUID userId,
                                             @RequestParam(value = "category", required = false) String category) {
        List<ClothesItem> items;
        if (category != null && !category.trim().isEmpty()) {
            items = clothesItemRepository.findByUserIdAndCategory(userId, category.trim().toUpperCase());
        } else {
            items = clothesItemRepository.findByUserId(userId);
        }
        return ResponseEntity.ok(items);
    }

    /**
     * Deletes a clothing item from the database.
     */
    @DeleteMapping("/items/{id}")
    public ResponseEntity<?> deleteClothesItem(@PathVariable("id") UUID id) {
        Optional<ClothesItem> itemOpt = clothesItemRepository.findById(id);
        if (itemOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Item not found."));
        }
        clothesItemRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Item deleted successfully."));
    }
}
