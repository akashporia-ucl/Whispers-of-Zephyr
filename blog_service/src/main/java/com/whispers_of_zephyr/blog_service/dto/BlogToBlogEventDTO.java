package com.whispers_of_zephyr.blog_service.dto;

import org.springframework.stereotype.Component;

import com.whispers_of_zephyr.blog_service.component.BlogEvent;
import com.whispers_of_zephyr.blog_service.model.Blog;

import jakarta.validation.constraints.NotNull;

@Component
public class BlogToBlogEventDTO {

    public BlogEvent toBlogEvent(@NotNull Blog blog) {
        BlogEvent event = new BlogEvent();
        event.setBlogAuthor(blog.getAuthor());
        event.setBlogCreatedTime(blog.getCreatedAt());
        event.setBlogId(blog.getId());
        event.setBlogTitle(blog.getTitle());
        return event;
    }

}
