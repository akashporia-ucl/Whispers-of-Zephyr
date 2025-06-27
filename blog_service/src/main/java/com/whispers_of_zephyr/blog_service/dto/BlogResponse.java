package com.whispers_of_zephyr.blog_service.dto;

public record BlogResponse(
        String title,
        String content,
        String author,
        String imageURL) {

}