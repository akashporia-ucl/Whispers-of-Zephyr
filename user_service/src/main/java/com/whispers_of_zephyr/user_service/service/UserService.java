package com.whispers_of_zephyr.user_service.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.whispers_of_zephyr.user_service.model.User;
import com.whispers_of_zephyr.user_service.repository.UserRepository;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class UserService {

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User createNewUser(User user) {
        log.info("Creating new user with username: " + user.getUsername());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        log.info("User created successfully");
        return userRepository.save(user);
    }

    public boolean validatePassword(String password, String encodedPassword) {
        log.info("Validating password");
        return passwordEncoder.matches(password, encodedPassword);
    }

    public String generateToken(User user) {
        log.info("Generating token for user: " + user.getUsername());
        return "";
    }

}
