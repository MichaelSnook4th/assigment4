package org.cst8277.snookmichael.assigment4.controllers;

import org.cst8277.snookmichael.assigment4.services.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TokenController {

    private final AuthService authService;

    public TokenController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/validate-token")
    public ResponseEntity<?> validateToken(@RequestParam String token) {
        // Validate the token
        boolean isValid = authService.isTokenValid(token);

        if (!isValid) {
            return ResponseEntity.status(401).body("Invalid or expired token");
        }

        // Retrieve roles associated with the token
        String roles = authService.getRolesByToken(token);

        return ResponseEntity.ok(roles);
    }
}