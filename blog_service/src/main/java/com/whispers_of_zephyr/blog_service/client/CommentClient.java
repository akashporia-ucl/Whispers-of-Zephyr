package com.whispers_of_zephyr.blog_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "comment-service", url = "${application.config.comment-service.url}")
public interface CommentClient {

    @GetMapping("/")
    public String helloComments();
}
