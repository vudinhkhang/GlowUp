package com.glowup.service;

import com.glowup.model.ClothesItem;
import org.springframework.stereotype.Service;

@Service
public class ColorHarmonyService {

    /**
     * Converts a Hex color string (e.g. "#FFA500" or "FFA500") to HSL values.
     * Returns double[] where index 0 = Hue (0-360), 1 = Saturation (0-100), 2 = Lightness (0-100).
     */
    public double[] hexToHsl(String hex) {
        if (hex == null || hex.trim().isEmpty()) {
            return new double[] { 0, 0, 100 }; // Default white
        }
        
        String cleanHex = hex.replace("#", "").trim();
        if (cleanHex.length() == 3) {
            cleanHex = "" + cleanHex.charAt(0) + cleanHex.charAt(0) +
                           cleanHex.charAt(1) + cleanHex.charAt(1) +
                           cleanHex.charAt(2) + cleanHex.charAt(2);
        }
        
        if (cleanHex.length() != 6) {
            return new double[] { 0, 0, 100 }; // Default fallback
        }

        try {
            int r = Integer.parseInt(cleanHex.substring(0, 2), 16);
            int g = Integer.parseInt(cleanHex.substring(2, 4), 16);
            int b = Integer.parseInt(cleanHex.substring(4, 6), 16);

            double rNorm = r / 255.0;
            double gNorm = g / 255.0;
            double bNorm = b / 255.0;

            double max = Math.max(rNorm, Math.max(gNorm, bNorm));
            double min = Math.min(rNorm, Math.min(gNorm, bNorm));

            double h = 0;
            double s = 0;
            double l = (max + min) / 2.0;

            if (max != min) {
                double d = max - min;
                s = l > 0.5 ? d / (2.0 - max - min) : d / (max + min);

                if (max == rNorm) {
                    h = (gNorm - bNorm) / d + (gNorm < bNorm ? 6 : 0);
                } else if (max == gNorm) {
                    h = (bNorm - rNorm) / d + 2;
                } else if (max == bNorm) {
                    h = (rNorm - gNorm) / d + 4;
                }
                h /= 6.0;
            }

            return new double[] { h * 360.0, s * 100.0, l * 100.0 };
        } catch (NumberFormatException e) {
            return new double[] { 0, 0, 100 };
        }
    }

    /**
     * Checks if a color is a Neutral (Black, White, Gray, Beige/Khaki, Denim/Navy).
     */
    public boolean isNeutral(double h, double s, double l) {
        // Black (Very low lightness)
        if (l <= 18) {
            return true;
        }
        // White (Very high lightness)
        if (l >= 85) {
            return true;
        }
        // Gray (Low saturation)
        if (s <= 10 && l >= 18 && l <= 85) {
            return true;
        }
        // Beige / Khaki (Hue around 20-50, low-medium saturation, medium-high lightness)
        if (h >= 20 && h <= 50 && s <= 28 && l >= 35 && l <= 82) {
            return true;
        }
        // Denim / Navy-Neutral (Hue around 200-240, low-medium saturation, low-medium lightness)
        if (h >= 200 && h <= 245 && s <= 30 && l >= 20 && l <= 60) {
            return true;
        }
        return false;
    }

    /**
     * Returns true if item1 and item2 satisfy a color harmony rule:
     * - Neutral rule (if either is neutral, they match)
     * - Monochromatic (same Hue, distinct Lightness)
     * - Analogous (adjacent Hues)
     * - Complementary (opposite Hues)
     */
    public boolean checkColorHarmony(ClothesItem item1, ClothesItem item2) {
        // Safe check
        if (item1 == null || item2 == null) {
            return false;
        }
        
        double h1 = item1.getHslHue() != null ? item1.getHslHue() : 0.0;
        double s1 = item1.getHslSaturation() != null ? item1.getHslSaturation() : 0.0;
        double l1 = item1.getHslLightness() != null ? item1.getHslLightness() : 100.0;

        double h2 = item2.getHslHue() != null ? item2.getHslHue() : 0.0;
        double s2 = item2.getHslSaturation() != null ? item2.getHslSaturation() : 0.0;
        double l2 = item2.getHslLightness() != null ? item2.getHslLightness() : 100.0;

        // Rule 1: Neutral rule - Neutrals pair with anything
        if (isNeutral(h1, s1, l1) || isNeutral(h2, s2, l2)) {
            return true;
        }

        // Calculate shortest distance in hue (circular 360-degree difference)
        double hueDiff = Math.abs(h1 - h2);
        if (hueDiff > 180.0) {
            hueDiff = 360.0 - hueDiff;
        }

        // Rule 2: Monochromatic (hue difference <= 15, and lightness difference >= 20 to create contrast)
        if (hueDiff <= 15.0) {
            return Math.abs(l1 - l2) >= 20.0;
        }

        // Rule 3: Analogous (hue difference between 15 and 45 degrees)
        if (hueDiff > 15.0 && hueDiff <= 45.0) {
            return true;
        }

        // Rule 4: Complementary (hue difference opposite, i.e., 165 to 180 degrees)
        if (hueDiff >= 165.0 && hueDiff <= 180.0) {
            return true;
        }

        // Rule 5: Split-Complementary (hue difference between 140 and 165 degrees)
        if (hueDiff >= 140.0 && hueDiff < 165.0) {
            return true;
        }

        return false;
    }
}
