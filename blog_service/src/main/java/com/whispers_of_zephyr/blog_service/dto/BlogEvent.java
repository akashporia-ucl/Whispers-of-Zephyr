package com.whispers_of_zephyr.blog_service.dto;

import java.io.Serializable;
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

    private String blogTitle;
    private String blogAuthor;
    private String blogContent;
    private UUID userId;
    private UUID imageUUID;
}
