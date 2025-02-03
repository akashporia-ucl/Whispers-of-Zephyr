package com.whispers_of_zephyr.user_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import com.whispers_of_zephyr.user_service.model.OTPRequest;

@FeignClient(name = "notification-service", url = "${application.config.notification-service.url}")
public interface NotificationClient {

    @GetMapping("/sendOtp")
    void sendOTP(OTPRequest request);
}
