package org.cst8277.snookmichael.assigment4.services;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.cst8277.snookmichael.assigment4.dtos.User;

import javax.crypto.SecretKey;
import org.cst8277.snookmichael.assigment4.dtos.Roles;
import java.util.Date;
import java.util.stream.Collectors;

public class JwtUtil {

    // Generate a more secure secret key
    private static final SecretKey SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    public String generateToken(User user) {
        return Jwts.builder()
                .setSubject(user.getId().toString())
                .claim("name", user.getName())
                .claim("email", user.getEmail())
                .claim("roles", user.getRoles().stream().map(Roles::getRoleName).collect(Collectors.toList()))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000)) // Token valid for 1 hour
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    public static String createJWT(String name, String email) {
        // Logic to create a JWT string based on the name and email
        return "dummyJWT"; // Replace with actual JWT creation logic
    }

    public static void verifyJWT(String token) {
        // Implement verification logic
        Jwts.parserBuilder().setSigningKey(SECRET_KEY).build().parseClaimsJws(token); // Use the secure key for verifying
    }
}