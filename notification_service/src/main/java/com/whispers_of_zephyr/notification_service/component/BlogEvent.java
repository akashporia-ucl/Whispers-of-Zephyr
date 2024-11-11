package com.whispers_of_zephyr.notification_service.component;

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
public class BlogEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    private UUID blogId;
    private String blogTitle;
    private String blogAuthor;
    private LocalDateTime blogCreatedTime;
}
