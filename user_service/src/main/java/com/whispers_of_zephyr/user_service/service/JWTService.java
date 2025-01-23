package com.whispers_of_zephyr.user_service.service;

import java.util.Date;

import org.springframework.stereotype.Service;

import com.whispers_of_zephyr.user_service.components.JWTSecretKeyGenerator;
import com.whispers_of_zephyr.user_service.model.User;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class JWTService {

    public String generateToken(User user, String role) {
        log.info("Generating token for user: " + user.getUsername());
        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim("roles", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(SignatureAlgorithm.RS256, JWTSecretKeyGenerator.getPrivateKey())
                .compact();
    }
}
