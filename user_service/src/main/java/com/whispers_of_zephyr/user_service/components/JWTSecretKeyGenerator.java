package com.whispers_of_zephyr.user_service.components;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.util.Base64;

import org.springframework.stereotype.Component;

import lombok.extern.log4j.Log4j2;

@Component
@Log4j2
public class JWTSecretKeyGenerator {

    private static KeyPair keyPair;

    private static void generateKeyPair() throws NoSuchAlgorithmException {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            keyPair = keyPairGenerator.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            log.info("Error generating key pair as {}", e.getMessage());
            throw new NoSuchAlgorithmException("Error generating key pair");
        }
    }

    public static PrivateKey getPrivateKey() {
        if (keyPair == null) {
            try {
                generateKeyPair();
            } catch (NoSuchAlgorithmException e) {
                log.info("Error generating key pair as {}", e.getMessage());
            }
        }
        return keyPair.getPrivate();
    }

    public static String getPublicKey() {
        if (keyPair == null) {
            try {
                generateKeyPair();
            } catch (NoSuchAlgorithmException e) {
                log.info("Error generating key pair as {}", e.getMessage());
            }
        }
        return Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
    }
}
