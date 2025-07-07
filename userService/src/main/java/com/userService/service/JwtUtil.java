package com.userService.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtUtil {

    @Value("${jwt.expiration}")
    private Long expiration;

    private final KeyPairProvider keyProvider;

    public String generateToken(String email, List<String> roles) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", roles);
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        long expMillis = nowMillis + expiration;
        Date exp = new Date(expMillis);

        return Jwts.builder()
                .setHeaderParam(JwsHeader.KEY_ID,keyProvider.getKeyId())
                .setClaims(claims)
                .setSubject(email)
                .setIssuedAt(now)
                .setIssuer("http://localhost:8081")
                .setExpiration(exp)
                .signWith(keyProvider.getPrivateKey(), SignatureAlgorithm.RS256)
                .compact();
    }

}
