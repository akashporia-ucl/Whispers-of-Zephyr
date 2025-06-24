package com.whispers_of_zephyr.blog_service.component;

import org.springframework.stereotype.Component;

import com.whispers_of_zephyr.blog_service.dto.BlogEvent;
import com.whispers_of_zephyr.blog_service.dto.BlogRequest;
import com.whispers_of_zephyr.blog_service.model.Blog;

import jakarta.validation.constraints.NotNull;

@Component
public class BlogRequestToBlogEventDTO {

    public BlogEvent toBlogEvent(@NotNull BlogRequest blogRequest) {
        BlogEvent event = new BlogEvent();
        event.setBlogAuthor(blogRequest.getAuthor());
        event.setBlogTitle(blogRequest.getTitle());
        event.setBlogContent(blogRequest.getContent());
        return event;
    }

}
