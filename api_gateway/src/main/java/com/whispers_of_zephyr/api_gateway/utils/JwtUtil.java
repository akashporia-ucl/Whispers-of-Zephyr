package com.whispers_of_zephyr.api_gateway.utils;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.whispers_of_zephyr.api_gateway.client.UserClient;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;

@Component
@Log4j2
public class JwtUtil {

    private final ApplicationContext applicationContext;

    public JwtUtil(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    // Fetch Feign client lazily to avoid circular dependency
    private UserClient getUserClient() {
        return applicationContext.getBean(UserClient.class);
    }

    // Fetch public key reactively without blocking
    private Mono<PublicKey> fetchPublicKey() {
        return Mono.just(getUserClient().getPublicKey()) // Wrap in Mono.just() to work with WebFlux
                .flatMap(publicKeyString -> {
                    try {
                        log.info("Public Key Fetched: {}", publicKeyString);

                        String cleanPublicKey = publicKeyString.replaceAll("-----BEGIN PUBLIC KEY-----", "")
                                .replaceAll("-----END PUBLIC KEY-----", "")
                                .replaceAll("\\s+", "");

                        byte[] publicBytes = Base64.getDecoder().decode(cleanPublicKey);
                        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicBytes);
                        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

                        return Mono.just(keyFactory.generatePublic(keySpec));
                    } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                        return Mono.error(new RuntimeException("Failed to convert public key", e));
                    }
                });
    }

    // Validate JWT Token reactively
    public Mono<Boolean> validateToken(String token) {
        return fetchPublicKey()
                .map(publicKey -> {
                    try {
                        log.info("Validating JWT Token: {}", token);

                        Jws<Claims> claims = Jwts.parserBuilder()
                                .setSigningKey(publicKey)
                                .build()
                                .parseClaimsJws(token);

                        boolean isValid = claims.getBody().getExpiration().after(new java.util.Date());
                        log.info("Is JWT valid? {}", isValid);
                        return isValid;
                    } catch (io.jsonwebtoken.ExpiredJwtException | io.jsonwebtoken.SignatureException
                            | io.jsonwebtoken.MalformedJwtException e) {
                        log.error("JWT validation failed", e);
                        return false;
                    }
                });
    }
}
