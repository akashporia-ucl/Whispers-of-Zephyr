package com.whispers_of_zephyr.user_service.components;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

import org.springframework.stereotype.Component;

import lombok.extern.log4j.Log4j2;

@Component
@Log4j2
public class JWTSecretKeyGenerator {

    private static KeyPair keyPair;

    static {
        try {
            generateKeyPair(); // Generate once at startup
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error generating key pair", e);
        }
    }

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
        log.info("Getting private key");
        return keyPair.getPrivate();
    }

    public static PublicKey getPublicKeyObject() {
        return keyPair.getPublic();
    }

    public static String getPublicKeyPEM() {
        PublicKey publicKey = keyPair.getPublic();
        String base64PublicKey = Base64.getEncoder().encodeToString(publicKey.getEncoded());
        return "-----BEGIN PUBLIC KEY-----\n" +
                base64PublicKey.replaceAll("(.{64})", "$1\n") +
                "\n-----END PUBLIC KEY-----";
    }
}
