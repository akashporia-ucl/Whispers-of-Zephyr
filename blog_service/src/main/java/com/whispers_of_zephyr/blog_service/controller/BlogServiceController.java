package com.whispers_of_zephyr.blog_service.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BlogServiceController {

    @GetMapping("/")
    public String sayHello() {
        return "Hello world!";
    }

}
