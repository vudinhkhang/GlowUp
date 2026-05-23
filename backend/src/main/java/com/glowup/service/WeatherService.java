package com.glowup.service;

import com.glowup.dto.WeatherInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WeatherService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${glowup.weather.api-key:mock}")
    private String apiKey;

    @Value("${glowup.weather.base-url:https://api.openweathermap.org/data/2.5}")
    private String baseUrl;

    // Static variables for dynamic testing in mock mode
    private static double mockTemperature = 22.0;
    private static String mockCondition = "Clear";

    public WeatherInfo getWeather(String city) {
        if (city == null || city.trim().isEmpty()) {
            city = "New York";
        }
        if ("mock".equalsIgnoreCase(apiKey)) {
            return new WeatherInfo(mockTemperature, mockCondition);
        }
        
        try {
            // https://api.openweathermap.org/data/2.5/weather?q={city}&appid={apiKey}&units=metric
            String url = String.format("%s/weather?q=%s&appid=%s&units=metric", baseUrl, city, apiKey);
            OpenWeatherMapResponse response = restTemplate.getForObject(url, OpenWeatherMapResponse.class);
            if (response != null && response.main() != null) {
                String condition = "Clear";
                if (response.weather() != null && response.weather().length > 0) {
                    condition = response.weather()[0].main();
                }
                return new WeatherInfo(response.main().temp(), condition);
            }
        } catch (Exception e) {
            System.err.println("OpenWeatherMap call failed: " + e.getMessage() + ". Falling back to mock weather.");
        }
        return new WeatherInfo(mockTemperature, mockCondition);
    }

    public void setMockWeather(double temp, String condition) {
        mockTemperature = temp;
        mockCondition = condition;
    }

    public double getMockTemperature() {
        return mockTemperature;
    }

    public String getMockCondition() {
        return mockCondition;
    }

    // OpenWeatherMap JSON mapped structures
    private static record OpenWeatherMapResponse(Main main, Weather[] weather) {}
    private static record Main(double temp) {}
    private static record Weather(String main) {}
}
