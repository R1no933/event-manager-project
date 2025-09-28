package dev.baskakov.eventmanagerservice.security.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtTokenManager {
    private final long expTime;
    private final SecretKey secretKey;

    public JwtTokenManager(
            @Value("${jwt.exptime}") long expTime,
            @Value("${jwt.secretkey}") String secretKeyString
    ) {
        this.expTime = expTime;
        this.secretKey = Keys.hmacShaKeyFor(secretKeyString.getBytes());
    }

    public String generateToken(String login){
        return Jwts
                .builder()
                .subject(login)
                .signWith(secretKey)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expTime))
                .compact();
    }

    public String getLoginFromToken(String token){
        return Jwts
                .parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }
}
