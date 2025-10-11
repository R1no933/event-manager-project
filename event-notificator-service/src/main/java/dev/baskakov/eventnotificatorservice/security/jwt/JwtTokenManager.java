package dev.baskakov.eventnotificatorservice.security.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

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


    public String getLoginFromToken(String token){
        return Jwts
                .parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public Long getUserIdFromToken(String token){
        return Jwts
                .parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("userId",  Long.class);
    }

    public String getUserRoleFromToken(String token){
        return Jwts
                .parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("userRole",  String.class);
    }
}
