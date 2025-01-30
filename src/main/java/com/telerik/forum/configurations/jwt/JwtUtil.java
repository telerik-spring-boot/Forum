package com.telerik.forum.configurations.jwt;


import com.telerik.forum.exceptions.UnauthorizedOperationException;
import com.telerik.forum.models.user.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.Objects;

@Component
public class JwtUtil {

    private final String SECRET_KEY;

    @Autowired
    public JwtUtil(Environment env) {
        SECRET_KEY = Base64.getEncoder().encodeToString(Objects.requireNonNull(env.getProperty("ENCRYPTION_SECRET_KEY")).getBytes(StandardCharsets.UTF_8));
    }



    public String generateToken(String username) {

        Key key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(SECRET_KEY));

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 5)) // 5 minutes for testing
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }


    public String extractUsername(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(Base64.getDecoder().decode(SECRET_KEY))
                .build()
                .parseClaimsJws(token)
                .getBody();

        if (claims.getExpiration().after(new Date())) {
            return claims.getSubject();
        }

        throw new UnauthorizedOperationException("Token expired.");
    }

}
