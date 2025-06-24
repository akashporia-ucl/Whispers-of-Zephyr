package com.whispers_of_zephyr.user_service.service;

import java.util.List;
import java.util.Optional;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.whispers_of_zephyr.user_service.dto.UserToUserEvent;
import com.whispers_of_zephyr.user_service.model.ConfirmPasswordRequest;
import com.whispers_of_zephyr.user_service.model.NewUserEvent;
import com.whispers_of_zephyr.user_service.model.ResetPasswordRequest;
import com.whispers_of_zephyr.user_service.model.User;
import com.whispers_of_zephyr.user_service.repository.UserRepository;

import jakarta.validation.ValidationException;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class UserService {

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final PasswordEncoder passwordEncoder;

    @Autowired
    private final JWTService jwtService;

    @Autowired
    private final RabbitTemplate rabbitTemplate;

    @Autowired
    private final UserToUserEvent userToUserEvent;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JWTService jwtService,
            RabbitTemplate rabbitTemplate, UserToUserEvent userToUserEvent) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.rabbitTemplate = rabbitTemplate;
        this.userToUserEvent = userToUserEvent;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User createNewUser(User user) {
        log.info("Creating new user with username: " + user.getUsername());
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            log.error("User already exists");
            throw new ValidationException("User already exists");
        }
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            log.error("Email already exists");
            throw new ValidationException("Email already exists");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        log.info("User created successfully");
        publishNewUserEvent(user);
        return userRepository.save(user);
    }

    public boolean validatePassword(String password, String encodedPassword) {
        log.info("Validating password");
        boolean validatePassword = passwordEncoder.matches(password, encodedPassword);
        log.info("Password validation result: " + validatePassword);
        return validatePassword;
    }

    public String generateToken(String username, String password) throws Exception {
        log.info("Generating token for user: " + username);
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            if (validatePassword(password, user.get().getPassword())) {
                return jwtService.generateToken(user.get());
            } else {
                throw new Exception("Invalid password");
            }
        } else {
            throw new Exception("User not found");
        }
    }

    public boolean checkUserExists(ResetPasswordRequest request) {
        log.info("Checking if user exists: " + request.getUsername());
        return userRepository.findByUsername(request.getUsername()).isPresent()
                || userRepository.findByEmail(request.getEmail()).isPresent();
    }

    public void resetPassword(ConfirmPasswordRequest request) {
        log.info("Resetting password for user: " + request.getUsername());
        User user = userRepository.findByUsername(request.getUsername()).get();
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);
        log.info("Password reset successfully");
    }

    private void publishNewUserEvent(User user) {
        log.info("Publishing new user event to the RabbitMQ");
        // NewUserEvent newUserEvent = userToUserEvent.toNewUserEvent(user);
        // rabbitTemplate.convertAndSend("userExchange", "user.created", newUserEvent);
    }
}
