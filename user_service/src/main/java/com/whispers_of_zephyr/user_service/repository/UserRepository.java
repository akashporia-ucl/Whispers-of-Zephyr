package com.whispers_of_zephyr.user_service.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.whispers_of_zephyr.user_service.model.User;

public interface UserRepository extends JpaRepository<User, UUID> {

    public Optional<User> findByUsername(String username);

    public Optional<User> findByEmail(String email);
}
