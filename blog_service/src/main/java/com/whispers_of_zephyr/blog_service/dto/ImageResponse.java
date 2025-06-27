package com.whispers_of_zephyr.blog_service.dto;

public record ImageResponse(
                String imageURL,
                String mimeType,
                String fileName,
                Integer size) {

}
