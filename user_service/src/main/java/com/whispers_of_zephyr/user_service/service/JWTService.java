package com.whispers_of_zephyr.user_service.service;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;

import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.whispers_of_zephyr.user_service.components.JWTSecretKeyGenerator;
import com.whispers_of_zephyr.user_service.model.User;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class JWTService {

    public String generateToken(User user) {
        log.info("Generating token for user: " + user.getUsername());

        RSAPrivateKey privateKey = (RSAPrivateKey) JWTSecretKeyGenerator.getPrivateKey();
        RSAPublicKey publicKey = (RSAPublicKey) JWTSecretKeyGenerator.getPublicKeyObject(); // Ensure you use the public
                                                                                            // key object

        Algorithm algorithm = Algorithm.RSA256(publicKey, privateKey); // Use both keys

        return JWT.create()
                .withSubject(user.getUsername()) // Set the subject (username)
                .withIssuedAt(new Date()) // Set the issued time
                .withExpiresAt(new Date(System.currentTimeMillis() + 3600000)) // Set expiration time (1 hour)
                .sign(algorithm);
    }

    public String getPublicKey() {
        log.info("Getting public key");
        return JWTSecretKeyGenerator.getPublicKeyPEM(); // Use PEM format
    }
}
