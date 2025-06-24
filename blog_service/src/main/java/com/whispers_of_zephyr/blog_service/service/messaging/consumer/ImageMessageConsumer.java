package com.whispers_of_zephyr.blog_service.service.messaging.consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.UUID;

import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import com.whispers_of_zephyr.blog_service.service.MinioService;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
@AllArgsConstructor
public class ImageMessageConsumer {

    @Autowired
    private MinioService minioService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    private final String DELETE_QUEUE = "blog-service.blog.delete";

    private final String DELETE_DLQ_QUEUE = "blog-service.blog.delete.dlq";

    private final String BLOG_DLQ_EXCHANGE_NAME = "blogDLQExchange";

    @RabbitListener(queues = DELETE_QUEUE)
    public void deleteUploadedImage(UUID imageUUID) {
        log.info("Received image delete message: {}", imageUUID);
        try {
            log.info("Processing image deletion for UUID: {}", imageUUID);
            minioService.deleteFile(imageUUID);
            log.info("Image deleted successfully: {}", imageUUID);
        } catch (Exception e) {
            log.error("Error deleting image: {}", e.getMessage());
            // Publish to DLQ
            publishToDLQ(imageUUID);
            throw new AmqpRejectAndDontRequeueException("Error deleting image, sent to DLQ");
        }
    }

    private void publishToDLQ(UUID imageUUID) {
        try {
            log.info("Publishing to DLQ: {}", imageUUID);
            rabbitTemplate.convertAndSend(BLOG_DLQ_EXCHANGE_NAME, DELETE_DLQ_QUEUE, imageUUID);
        } catch (Exception e) {
            log.error("Failed to publish to DLQ: {}", e.getMessage());
        }
    }
}
