package com.whispers_of_zephyr.user_service.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ConfirmPasswordRequest {

    private String username;

    private String email;

    private String password;

    private String confirmPassword;

    private String otp;

}
