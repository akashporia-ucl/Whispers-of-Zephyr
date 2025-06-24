package com.whispers_of_zephyr.blog_service.service.messaging.producer;

import java.util.UUID;

import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.whispers_of_zephyr.blog_service.component.BlogRequestToBlogEventDTO;
import com.whispers_of_zephyr.blog_service.dto.BlogEvent;
import com.whispers_of_zephyr.blog_service.dto.BlogRequest;
import com.whispers_of_zephyr.blog_service.model.Blog;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class BlogMessageProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private BlogRequestToBlogEventDTO blogToBlogEventDTO;

    private final String BLOG_EXCHANGE_NAME = "blogExchange";

    private final String CREATE_QUEUE = "blog-service.blog.create";

    private final String DELETE_QUEUE = "blog-service.blog.delete";

    public boolean sendBlogCreateMessage(BlogRequest blogRequest, UUID userId, UUID imageUUID) {
        try {
            BlogEvent blogEvent = blogToBlogEventDTO.toBlogEvent(blogRequest);
            blogEvent.setUserId(userId);
            blogEvent.setImageUUID(imageUUID);
            log.info("Sending blog create message: {}", blogRequest);
            rabbitTemplate.convertAndSend(BLOG_EXCHANGE_NAME, CREATE_QUEUE, blogEvent);
            return true;
        } catch (Exception e) {
            log.error("Failed to send blog create message: {}", e.getMessage());
            rabbitTemplate.convertAndSend(BLOG_EXCHANGE_NAME, DELETE_QUEUE, imageUUID);
            return false;
        }
    }

}
