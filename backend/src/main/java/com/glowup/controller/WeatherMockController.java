package com.glowup.controller;

import com.glowup.service.WeatherService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/weather/mock")
public class WeatherMockController {

    private final WeatherService weatherService;

    public WeatherMockController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    /**
     * Updates the mock weather values.
     */
    @PostMapping
    public ResponseEntity<?> updateMockWeather(@RequestBody Map<String, Object> payload) {
        if (!payload.containsKey("temp") || !payload.containsKey("condition")) {
            return ResponseEntity.badRequest().body(Map.of("message", "Temp and condition are required."));
        }
        
        double temp = ((Number) payload.get("temp")).doubleValue();
        String condition = (String) payload.get("condition");
        
        weatherService.setMockWeather(temp, condition);
        return ResponseEntity.ok(Map.of(
                "message", "Mock weather updated successfully.",
                "temp", temp,
                "condition", condition
        ));
    }

    /**
     * Gets current mock weather values.
     */
    @GetMapping
    public ResponseEntity<?> getMockWeather() {
        return ResponseEntity.ok(Map.of(
                "temp", weatherService.getMockTemperature(),
                "condition", weatherService.getMockCondition()
        ));
    }
}
