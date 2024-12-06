package org.cst8277.snookmichael.assigment4.controllers;

import org.cst8277.snookmichael.assigment4.services.TokenValidationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TargetServiceController {

    private final TokenValidationService tokenValidationService;

    public TargetServiceController(TokenValidationService tokenValidationService) {
        this.tokenValidationService = tokenValidationService;
    }

    @GetMapping("/secure-endpoint")
    public ResponseEntity<?> secureEndpoint(@RequestHeader("Authorization") String authHeader) {
        // Extract token from Authorization header
        String token = authHeader.replace("Bearer ", "");

        // Validate token with UMS
        boolean isValid = tokenValidationService.validateToken(token);

        if (!isValid) {
            return ResponseEntity.status(401).body("Unauthorized: Invalid or expired token");
        }

        // Token is valid, proceed with the request
        return ResponseEntity.ok("Request performed successfully");
    }
}
