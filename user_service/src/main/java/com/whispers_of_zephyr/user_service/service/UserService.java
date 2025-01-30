package com.whispers_of_zephyr.user_service.service;

import java.util.List;
import java.util.Optional;

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

    @Autowired
    private final JWTService jwtService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JWTService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
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

}
