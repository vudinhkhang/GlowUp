package com.glowup.service;

import com.glowup.dto.WeatherInfo;
import com.glowup.model.ClothesItem;
import com.glowup.model.Outfit;
import com.glowup.model.OutfitCalendar;
import com.glowup.repository.ClothesItemRepository;
import com.glowup.repository.OutfitCalendarRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StylingService {

    private final ClothesItemRepository clothesItemRepository;
    private final OutfitCalendarRepository calendarRepository;
    private final ColorHarmonyService colorHarmonyService;
    private final WeatherService weatherService;

    public StylingService(ClothesItemRepository clothesItemRepository,
                          OutfitCalendarRepository calendarRepository,
                          ColorHarmonyService colorHarmonyService,
                          WeatherService weatherService) {
        this.clothesItemRepository = clothesItemRepository;
        this.calendarRepository = calendarRepository;
        this.colorHarmonyService = colorHarmonyService;
        this.weatherService = weatherService;
    }

    /**
     * Recommends a list of outfits for a user based on location weather, color harmony, 
     * and user wear history (to promote sustainable closet rotation).
     */
    public List<OutfitRecommendation> getDailyRecommendations(UUID userId, String city) {
        WeatherInfo weather = weatherService.getWeather(city);
        double temp = weather.temp();

        List<ClothesItem> closet = clothesItemRepository.findByUserId(userId);
        if (closet.isEmpty()) {
            return Collections.emptyList();
        }

        // Get wear history for the last 30 days to calculate sustainable rotation
        List<OutfitCalendar> history = calendarRepository.findByUserId(userId);
        Map<UUID, Integer> wearCount = new HashMap<>();
        for (ClothesItem item : closet) {
            wearCount.put(item.getId(), 0);
        }
        for (OutfitCalendar log : history) {
            if (log.getDate().isAfter(LocalDate.now().minusDays(30)) && log.getOutfit() != null) {
                for (ClothesItem item : log.getOutfit().getItems()) {
                    wearCount.put(item.getId(), wearCount.getOrDefault(item.getId(), 0) + 1);
                }
            }
        }

        // Filter clothes by weather temperature
        List<ClothesItem> tops = new ArrayList<>();
        List<ClothesItem> bottoms = new ArrayList<>();
        List<ClothesItem> outerwear = new ArrayList<>();
        List<ClothesItem> dresses = new ArrayList<>();
        List<ClothesItem> shoes = new ArrayList<>();

        for (ClothesItem item : closet) {
            if (!isGarmentSuitableForWeather(item, temp)) {
                continue;
            }

            switch (item.getCategory().toUpperCase()) {
                case "TOP" -> tops.add(item);
                case "BOTTOM" -> bottoms.add(item);
                case "OUTERWEAR" -> outerwear.add(item);
                case "DRESS" -> dresses.add(item);
                case "SHOES" -> shoes.add(item);
            }
        }

        List<OutfitRecommendation> recommendations = new ArrayList<>();

        // Case A: Top + Bottom combinations
        for (ClothesItem top : tops) {
            for (ClothesItem bottom : bottoms) {
                // Color harmony check
                if (!colorHarmonyService.checkColorHarmony(top, bottom)) {
                    continue;
                }

                List<ClothesItem> outfitItems = new ArrayList<>();
                outfitItems.add(top);
                outfitItems.add(bottom);

                // Add Outerwear if temp < 16°C and outerwear list is not empty
                ClothesItem selectedOuterwear = null;
                if (temp < 16.0 && !outerwear.isEmpty()) {
                    for (ClothesItem out : outerwear) {
                        if (colorHarmonyService.checkColorHarmony(top, out) && 
                            colorHarmonyService.checkColorHarmony(bottom, out)) {
                            selectedOuterwear = out;
                            break;
                        }
                    }
                    if (selectedOuterwear != null) {
                        outfitItems.add(selectedOuterwear);
                    } else if (temp < 12.0) {
                        // In cold weather, outerwear is strictly required. Skip if we can't find harmonious outerwear.
                        continue;
                    }
                }

                // Add Shoes
                ClothesItem selectedShoes = null;
                if (!shoes.isEmpty()) {
                    for (ClothesItem s : shoes) {
                        if (colorHarmonyService.checkColorHarmony(top, s) || 
                            colorHarmonyService.checkColorHarmony(bottom, s)) {
                            selectedShoes = s;
                            break;
                        }
                    }
                    if (selectedShoes != null) {
                        outfitItems.add(selectedShoes);
                    }
                }

                double sustainabilityScore = calculateSustainabilityScore(outfitItems, wearCount);
                String harmonyType = determineHarmonyType(top, bottom);
                
                recommendations.add(new OutfitRecommendation(
                    outfitItems, 
                    sustainabilityScore, 
                    harmonyType, 
                    "Perfect for " + weather.condition() + " weather (" + String.format("%.1f", temp) + "°C)"
                ));
            }
        }

        // Case B: Dress combinations
        for (ClothesItem dress : dresses) {
            List<ClothesItem> outfitItems = new ArrayList<>();
            outfitItems.add(dress);

            // Add Outerwear if cold
            ClothesItem selectedOuterwear = null;
            if (temp < 16.0 && !outerwear.isEmpty()) {
                for (ClothesItem out : outerwear) {
                    if (colorHarmonyService.checkColorHarmony(dress, out)) {
                        selectedOuterwear = out;
                        break;
                    }
                }
                if (selectedOuterwear != null) {
                    outfitItems.add(selectedOuterwear);
                } else if (temp < 12.0) {
                    continue;
                }
            }

            // Add Shoes
            ClothesItem selectedShoes = null;
            if (!shoes.isEmpty()) {
                for (ClothesItem s : shoes) {
                    if (colorHarmonyService.checkColorHarmony(dress, s)) {
                        selectedShoes = s;
                        break;
                    }
                }
                if (selectedShoes != null) {
                    outfitItems.add(selectedShoes);
                }
            }

            double sustainabilityScore = calculateSustainabilityScore(outfitItems, wearCount);
            recommendations.add(new OutfitRecommendation(
                outfitItems, 
                sustainabilityScore, 
                "Dress Harmony", 
                "Elegant dress styling for " + String.format("%.1f", temp) + "°C"
            ));
        }

        // Sort recommendations by sustainability score descending (least worn first)
        recommendations.sort((o1, o2) -> Double.compare(o2.score(), o1.score()));

        // Limit to top 15 recommendations to prevent payload bloating
        return recommendations.stream().limit(15).collect(Collectors.toList());
    }

    /**
     * Determines whether a garment is suitable for a given temperature (in Celsius).
     */
    private boolean isGarmentSuitableForWeather(ClothesItem item, double temp) {
        String cat = item.getCategory().toUpperCase();
        List<String> seasons = item.getSeasonTags();

        // 1. Cold Weather (< 12°C)
        if (temp < 12.0) {
            if (seasons != null && !seasons.isEmpty() && 
                !seasons.contains("Winter") && !seasons.contains("Fall")) {
                return false;
            }
            if (cat.equals("TOP")) {
                // Skip light shortsleeves / tanktops unless layering (for MVP, exclude)
                String sub = item.getSubCategory().toLowerCase();
                if (sub.contains("tank") || sub.contains("crop") || sub.contains("short sleeve")) {
                    return false;
                }
            }
            if (cat.equals("DRESS")) {
                // Standard dresses are typically not warm enough for < 12C
                return false;
            }
            return true;
        }

        // 2. Hot Weather (> 22°C)
        if (temp > 22.0) {
            if (seasons != null && !seasons.isEmpty() && 
                !seasons.contains("Summer") && !seasons.contains("Spring")) {
                return false;
            }
            if (cat.equals("OUTERWEAR")) {
                // No coats or heavy jackets in hot weather
                String sub = item.getSubCategory().toLowerCase();
                return sub.contains("cardigan") || sub.contains("kimono") || sub.contains("light");
            }
            if (cat.equals("BOTTOM")) {
                // Avoid heavy/fleece pants
                String sub = item.getSubCategory().toLowerCase();
                return !sub.contains("fleece") && !sub.contains("wool");
            }
            return true;
        }

        // 3. Mild Weather (12°C to 22°C)
        return seasons == null || seasons.isEmpty() || 
               seasons.contains("Spring") || seasons.contains("Fall") || seasons.contains("Winter");
    }

    /**
     * Sustainability score calculation.
     * The score increases for items that have been worn FEWER times.
     * Score ranges from 0 to 100.
     */
    private double calculateSustainabilityScore(List<ClothesItem> items, Map<UUID, Integer> wearCount) {
        if (items.isEmpty()) return 100.0;
        
        double totalWear = 0.0;
        for (ClothesItem item : items) {
            totalWear += wearCount.getOrDefault(item.getId(), 0);
        }
        
        double averageWear = totalWear / items.size();
        
        // Return a score: 100.0 if never worn, decreasing as wear increases
        return Math.max(0.0, 100.0 - (averageWear * 15.0));
    }

    /**
     * Identifies the primary color relationship type.
     */
    private String determineHarmonyType(ClothesItem item1, ClothesItem item2) {
        double h1 = item1.getHslHue() != null ? item1.getHslHue() : 0.0;
        double s1 = item1.getHslSaturation() != null ? item1.getHslSaturation() : 0.0;
        double l1 = item1.getHslLightness() != null ? item1.getHslLightness() : 100.0;

        double h2 = item2.getHslHue() != null ? item2.getHslHue() : 0.0;
        double s2 = item2.getHslSaturation() != null ? item2.getHslSaturation() : 0.0;
        double l2 = item2.getHslLightness() != null ? item2.getHslLightness() : 100.0;

        if (colorHarmonyService.isNeutral(h1, s1, l1) || colorHarmonyService.isNeutral(h2, s2, l2)) {
            return "Neutral Match (Classic)";
        }

        double hueDiff = Math.abs(h1 - h2);
        if (hueDiff > 180.0) {
            hueDiff = 360.0 - hueDiff;
        }

        if (hueDiff <= 15.0) {
            return "Monochromatic (Sleek)";
        }
        if (hueDiff <= 45.0) {
            return "Analogous (Harmonious)";
        }
        if (hueDiff >= 165.0) {
            return "Complementary (Vibrant)";
        }
        if (hueDiff >= 140.0) {
            return "Split-Complementary (Artistic)";
        }

        return "Custom Match";
    }

    // DTO for suggestions
    public static record OutfitRecommendation(
        List<ClothesItem> items,
        double score,
        String harmonyType,
        String weatherAdvice
    ) {}
}
