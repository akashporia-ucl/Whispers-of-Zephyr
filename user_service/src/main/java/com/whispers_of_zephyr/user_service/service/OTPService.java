package com.whispers_of_zephyr.user_service.service;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class OTPService {

    private final Map<String, String> otpMap = new HashMap<>();
    private final SecureRandom secureRandom = new SecureRandom();

    public String generateOtp(String email) {
        log.info("Generating OTP for email: " + email);
        byte[] randomBytes = new byte[6];
        secureRandom.nextBytes(randomBytes);
        String otp = Base64.getEncoder().encodeToString(randomBytes);
        log.info("Generated OTP: " + otp);
        otpMap.put(email, otp);
        return otp;
    }

    public boolean validateOtp(String email, String otp) {
        return otpMap.containsKey(email) && otpMap.get(email).equals(otp);
    }

    public void removeOtp(String email) {
        otpMap.remove(email);
    }

}
