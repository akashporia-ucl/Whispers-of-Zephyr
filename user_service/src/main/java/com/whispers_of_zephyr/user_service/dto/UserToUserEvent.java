package com.whispers_of_zephyr.user_service.dto;

import org.springframework.stereotype.Component;

import com.whispers_of_zephyr.user_service.model.NewUserEvent;
import com.whispers_of_zephyr.user_service.model.User;

@Component
public class UserToUserEvent {

    public NewUserEvent toNewUserEvent(User user) {
        NewUserEvent event = new NewUserEvent();
        event.setUserId(user.getId());
        event.setUsername(user.getUsername());
        event.setEmail(user.getEmail());
        event.setFullName(user.getFullName());
        event.setCreatedAt(user.getCreatedAt());
        return event;
    }
}
