package com.whispers_of_zephyr.user_service.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NewUserEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    private UUID userId;
    private String username;
    private String email;
    private String fullName;
    private LocalDateTime createdAt;
}
