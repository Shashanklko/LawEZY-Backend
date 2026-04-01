package com.LawEZY.auth.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component // Tells Spring to create one instance of this utility we can use anywhere
public class JwtUtil {

    // This is the private master key used to sign the tokens. NEVER share this or put it in a public repository!
    // In a real app, this should be stored in your application.properties or environment variables.
    private static final String SECRET_KEY_STRING = "ThisIsAMassiveSecretKeyThatMustBeAtLeast32CharactersLongToWorkProperly";
    
    private final SecretKey secretKey = Keys.hmacShaKeyFor(SECRET_KEY_STRING.getBytes(StandardCharsets.UTF_8));

    // Token is valid for 10 hours (1000ms * 60s * 60m * 10h)
    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 10;

    // 1. EXTRACT USERNAME (Email) FROM TOKEN
    public String extractUsername(String token) {
        return extractClaim(token, "sub", String.class);
    }

    // 2. EXTRACT EXPIRATION DATE FROM TOKEN
    public Date extractExpiration(String token) {
        return extractClaim(token, "exp", Date.class);
    }

    // Generic method to pull any specific piece of data (Claim) out of the token
    public <T> T extractClaim(String token, String claimName, Class<T> requiredType) {
        final Claims claims = extractAllClaims(token);
        return claims.get(claimName, requiredType);
    }

    // The core method that reads the token using our secret key
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // Check if the token has expired
    public Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // 3. GENERATE A NEW TOKEN FOR A USER
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>(); 
        String role = userDetails.getAuthorities().stream().findFirst().map(auth -> auth.getAuthority()).orElse("ROLE_CLIENT");
        claims.put("role",role);
        return createToken(claims, userDetails.getUsername());
    }

    // The core method that builds and cryptographically signs the final Token string
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(secretKey)
                .compact();
    }

    // 4. VALIDATE TOKEN (Check if it belongs to the user and is not expired)
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
