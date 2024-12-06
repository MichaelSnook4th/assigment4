package org.cst8277.snookmichael.assigment4.services;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class TokenValidationService {

    private static final String UMS_URL = "http://localhost:8080/validate-token";

    public boolean validateToken(String token) {
        RestTemplate restTemplate = new RestTemplate();
        String url = UMS_URL + "?token=" + token;

        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            return response.getStatusCode().is2xxSuccessful(); // Valid token
        } catch (Exception e) {
            return false; // Invalid or expired token
        }
    }
}
