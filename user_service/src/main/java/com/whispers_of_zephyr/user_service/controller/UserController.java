package com.whispers_of_zephyr.user_service.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.whispers_of_zephyr.user_service.model.LoginRequest;
import com.whispers_of_zephyr.user_service.model.User;
import com.whispers_of_zephyr.user_service.service.JWTService;
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

    public UserController(UserService userService, JWTService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
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

    @GetMapping("/user/login")
    public String getJwtToken(@RequestBody @NotNull LoginRequest loginRequest)
            throws Exception {
        log.info("Logging in user: {}", loginRequest);
        return userService.generateToken(loginRequest.getUsername(), loginRequest.getPassword());
    }

    @GetMapping("/public-key")
    public String getPublicKey() {
        log.info("Getting public key");
        return jwtService.getPublicKey();
    }
}
