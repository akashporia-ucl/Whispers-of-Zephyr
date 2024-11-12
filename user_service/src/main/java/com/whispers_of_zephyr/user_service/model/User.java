package com.whispers_of_zephyr.user_service.model;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "Full Name")
    @NotBlank(message = "Full Name is mandatory")
    private String fullName;

    @Column(name = "Username")
    @NotBlank(message = "Username is mandatory")
    private String username;

    @Column(name = "Email")
    @NotBlank(message = "Email is mandatory")
    private String email;

    @Column(name = "Password")
    @NotBlank(message = "Password is mandatory")
    private String password;

    @Column(name = "Created At")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

}
