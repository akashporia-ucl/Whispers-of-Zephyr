package com.whispers_of_zephyr.notification_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.whispers_of_zephyr.notification_service.service.NotificationService;

import lombok.extern.log4j.Log4j2;

@RestController
@RequestMapping("/notification-service/api/v1/")
@Log4j2
public class NotificationController {

    @Autowired
    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/")
    public String getMethodName() {
        return "Welcome to Notification Service";
    }

    @PostMapping("/welcomeMail")
    public ResponseEntity<String> welcomeEmail(@RequestParam String emailId) throws Exception {
        log.info("Sending welcome email to " + emailId);
        log.info("Calling notification service to send welcome email");
        notificationService.sendWelcomeEmail(emailId);
        log.info("Notification service called successfully");
        return ResponseEntity.status(200).body("Welcome email sent to " + emailId);
    }

    @PostMapping("/sendOtp")
    public String sendOTP(@RequestBody String entity) {
        log.info("Sending OTP to " + entity);

        return entity;
    }

}
