package com.whispers_of_zephyr.user_service.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.whispers_of_zephyr.user_service.client.NotificationClient;
import com.whispers_of_zephyr.user_service.model.ConfirmPasswordRequest;
import com.whispers_of_zephyr.user_service.model.LoginRequest;
import com.whispers_of_zephyr.user_service.model.OTPRequest;
import com.whispers_of_zephyr.user_service.model.ResetPasswordRequest;
import com.whispers_of_zephyr.user_service.model.User;
import com.whispers_of_zephyr.user_service.model.ValidateOtpRequest;
import com.whispers_of_zephyr.user_service.service.JWTService;
import com.whispers_of_zephyr.user_service.service.OTPService;
import com.whispers_of_zephyr.user_service.service.UserService;

import jakarta.validation.constraints.NotNull;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@RequestMapping("/user-service/api/v1")
public class UserController {

    @Autowired
    private final UserService userService;

    @Autowired
    private final JWTService jwtService;

    @Autowired
    private final OTPService otpService;

    @Autowired
    private final NotificationClient notificationClient;

    public UserController(UserService userService, JWTService jwtService, OTPService otpService,
            NotificationClient notificationClient) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.otpService = otpService;
        this.notificationClient = notificationClient;
    }

    @GetMapping("/user")
    public ResponseEntity<List<User>> getUsers() {
        log.info("Getting all users");
        List<User> users = userService.getAllUsers();
        return ResponseEntity.status(200).body(users);
    }

    @PostMapping("/user")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        log.info("Creating a new user: {}", user);
        User createdUser = userService.createNewUser(user);
        return ResponseEntity.status(200).body(createdUser);
    }

    @PostMapping("/user/login")
    public String getJwtToken(@RequestBody @NotNull LoginRequest loginRequest)
            throws Exception {
        log.info("Logging in user: {}", loginRequest);
        return userService.generateToken(loginRequest.getUsername(), loginRequest.getPassword());
    }

    @PostMapping("/user/auth/reset-password/request")
    public String resetUserPasswordRequest(@RequestBody ResetPasswordRequest request) throws NotFoundException {
        log.info("Requesting password reset for user: {}", request.getUsername());

        if (!userService.checkUserExists(request)) {
            throw new NotFoundException();
        }

        String otp = otpService.generateOtp(request.getEmail());

        OTPRequest otpRequest = new OTPRequest();
        otpRequest.setEmail(request.getEmail());
        otpRequest.setOtp(otp);
        notificationClient.sendOTP(otpRequest);

        return "OTP sent successfully";

    }

    @PostMapping("/user/auth/reset-password/validate-otp")
    public String resetUserPasswordValidateOtp(@RequestBody ValidateOtpRequest otp) {
        log.info("Validate otp for user: {}", otp);
        boolean isValid = otpService.validateOtp(otp.getEmail(), otp.getOtp());
        if (!isValid) {
            return "Invalid OTP";
        }
        return "OTP validated successfully";
    }

    @PostMapping("/user/auth/reset-password/confirm-password")
    public String resetUserPasswordConfirm(@RequestBody ConfirmPasswordRequest request) {
        log.info("Resetting password for user: {}", request.getUsername());
        boolean isValid = otpService.validateOtp(request.getEmail(), request.getOtp());
        if (!isValid) {
            return "Invalid OTP";
        }
        userService.resetPassword(request);
        otpService.removeOtp(request.getUsername());
        return "Password reset successfully";
    }

    @GetMapping("/public-key")
    public String getPublicKey() {
        log.info("Getting public key");
        return jwtService.getPublicKey();
    }
}
