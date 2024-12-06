package org.cst8277.snookmichael.assigment4.controllers;

import org.cst8277.snookmichael.assigment4.dao.UmsRepository;
import org.cst8277.snookmichael.assigment4.dtos.User;
import org.cst8277.snookmichael.assigment4.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.client.RestTemplate;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
public class AuthController {

    private final SecretKey secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private final UmsRepository umsRepository;
    private final AuthService authService;

    public AuthController(UmsRepository umsRepository, AuthService authService) {
        this.umsRepository = umsRepository;
        this.authService = authService;
    }

    @GetMapping("/login/oauth2/code/github")
    public String handleGitHubCallback(OAuth2AuthenticationToken token, HttpSession session) {
        Map<String, Object> attributes = token.getPrincipal().getAttributes();
        String name = (String) attributes.getOrDefault("name", attributes.get("login"));
        String email = (String) attributes.get("email");

        // JWT creation
        String jwt = Jwts.builder()
                .setSubject("GitHubUser")
                .claim("name", name)
                .claim("email", email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 15 * 60 * 1000)) // 15 minutes
                .signWith(secretKey)
                .compact();

        session.setAttribute("jwt", jwt);

        return "redirect:/home"; // Redirect to the home page
    }

    private String fetchEmailFromGitHubAPI(OAuth2AuthenticationToken token) {
        String accessToken = token.getPrincipal().getAttributes().get("access_token").toString();
        String url = "https://api.github.com/user/emails";

        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + accessToken);

            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity,
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );

            for (Map<String, Object> emailData : response.getBody()) {
                if ((boolean) emailData.get("primary") && (boolean) emailData.get("verified")) {
                    return emailData.get("email").toString();
                }
            }
        } catch (Exception e) {
            System.err.println("Error fetching email from GitHub API: " + e.getMessage());
        }
        return null;
    }

    @GetMapping("/decode-token")
    public Map<String, Object> decodeToken(@RequestParam String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
