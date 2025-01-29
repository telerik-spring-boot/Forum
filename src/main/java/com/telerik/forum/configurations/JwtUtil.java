package com.telerik.forum.configurations;


import com.telerik.forum.models.user.User;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtUtil {

    //This should be saved in the application.properties file
    private static final String SECRET_KEY = Base64.getEncoder().encodeToString(
            "thisisaverysecureandlongsecretkey!".getBytes(StandardCharsets.UTF_8)
    );

    public String generateToken(String username) {
        // Decode the Base64 key before using it
        Key key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(SECRET_KEY));

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 5)) // 5 minutes for testing
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }




    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(Base64.getDecoder().decode(SECRET_KEY))
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateToken(String token, User userDetails) {
        String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        Date expiration = Jwts.parserBuilder()
                .setSigningKey(Base64.getDecoder().decode(SECRET_KEY))
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
        return expiration.before(new Date());
    }
}
