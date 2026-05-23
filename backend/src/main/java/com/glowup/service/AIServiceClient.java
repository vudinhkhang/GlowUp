package com.glowup.service;

import com.glowup.dto.ScanResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class AIServiceClient {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${glowup.ai-service.url:http://localhost:8000}")
    private String aiServiceUrl;

    public ScanResult scanGarment(MultipartFile file) {
        try {
            String url = aiServiceUrl + "/remove-bg";

            // Prepare multipart body
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            ByteArrayResource fileResource = new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            };
            body.add("file", fileResource);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            // Execute POST request
            ResponseEntity<byte[]> response = restTemplate.postForEntity(url, requestEntity, byte[].class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                // Read X-Primary-Color header
                String primaryColor = response.getHeaders().getFirst("X-Primary-Color");
                if (primaryColor == null) {
                    primaryColor = "#FFFFFF"; // Fallback
                }
                return new ScanResult(response.getBody(), primaryColor);
            } else {
                throw new RuntimeException("AI scan service returned status: " + response.getStatusCode());
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read upload file bytes", e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to connect to AI scan service: " + e.getMessage(), e);
        }
    }
}
