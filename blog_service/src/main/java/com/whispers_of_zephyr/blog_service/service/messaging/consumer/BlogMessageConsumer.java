package com.whispers_of_zephyr.blog_service.service.messaging.consumer;

import java.util.UUID;

import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.whispers_of_zephyr.blog_service.dto.BlogEvent;
import com.whispers_of_zephyr.blog_service.model.Blog;
import com.whispers_of_zephyr.blog_service.service.BlogService;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class BlogMessageConsumer {

    @Autowired
    private BlogService blogService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    private final String CREATE_QUEUE = "blog-service.blog.create";

    private final String CREATE_DLQ_QUEUE = "blog-service.blog.create.dlq";

    private final String DELETE_QUEUE = "blog-service.blog.delete";

    private final String BLOG_EXCHANGE_NAME = "blogExchange";

    private final String BLOG_DLQ_EXCHANGE_NAME = "blogDLQExchange";

    @RabbitListener(queues = CREATE_QUEUE)
    public void consumeBlogMessage(BlogEvent blogEvent) {
        log.info("Received blog create message: {}", blogEvent);
        try {
            log.info("Processing blog creation for: {}", blogEvent);

            // Validate the blog event
            if (!isValidRequest(blogEvent)) {
                log.error("Invalid blog event received: {}", blogEvent);
                // Publihsh a msg to DLQ
                throw new AmqpRejectAndDontRequeueException("Invalid request data");
            }

            Blog createBlog = new Blog(
                    blogEvent.getBlogTitle(),
                    blogEvent.getBlogContent(),
                    blogEvent.getBlogAuthor(),
                    blogEvent.getUserId());

            try {
                Blog createdBlog = blogService.postBlog(createBlog, blogEvent.getImageUUID());
                log.info("Blog created successfully: {}", createdBlog);
            } catch (Exception e) {
                log.error("Error creating blog: {}", e.getMessage());
                if (isTransientError(e)) {
                    log.info("Transient error occurred, sending message to DLQ for retry: {}", e.getMessage());
                    publishToDLQ(blogEvent);
                    throw new AmqpRejectAndDontRequeueException("Transient error occurred, retrying later");
                } else {
                    log.info("Non-transient error occurred, not retrying: {}", e.getMessage());
                    log.info("Initiating compensation logic for blog creation failure");
                    initiateCompensation(blogEvent.getImageUUID());
                }
                throw new AmqpRejectAndDontRequeueException("Error creating blog");
            }

        } catch (AmqpRejectAndDontRequeueException e) {
            log.error("Failed to create blog: {}", e.getMessage());
            publishToDLQ(blogEvent);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error occurred while processing blog event: {}", e.getMessage());
            publishToDLQ(blogEvent);
            throw new AmqpRejectAndDontRequeueException("Unexpected error occurred", e);
        }
    }

    private boolean isValidRequest(BlogEvent blogEvent) {
        // Add validation logic for the blog event
        return blogEvent != null
                && blogEvent.getBlogTitle() != null
                && !blogEvent.getBlogTitle().isEmpty()
                && blogEvent.getBlogContent() != null
                && !blogEvent.getBlogContent().isEmpty()
                && blogEvent.getBlogAuthor() != null
                && !blogEvent.getBlogAuthor().isEmpty()
                && blogEvent.getUserId() != null;
    }

    private boolean isTransientError(Exception e) {
        // Define logic to determine if the error is transient
        // For example, check for specific exception types or messages
        return e.getMessage().contains("Connection timeout") ||
                e.getMessage().contains("Too many connections") ||
                e.getMessage().contains("Lock wait timeout") ||
                e instanceof java.net.ConnectException ||
                e instanceof java.sql.SQLTransientException;
    }

    private void initiateCompensation(UUID imageUUID) {
        try {
            rabbitTemplate.convertAndSend(BLOG_EXCHANGE_NAME, DELETE_QUEUE, imageUUID);
            log.info("Compensation logic executed successfully");
        } catch (Exception e) {
            log.error("Error during compensation logic: {}", e.getMessage());

        }
    }

    private void publishToDLQ(BlogEvent blogEvent) {
        log.info("Publishing blog event to DLQ: {}", blogEvent);

        try {
            rabbitTemplate.convertAndSend(BLOG_DLQ_EXCHANGE_NAME, CREATE_DLQ_QUEUE, blogEvent);
            log.info("Blog event published to DLQ successfully");
        } catch (Exception e) {
            log.error("Failed to publish blog event to DLQ: {}", e.getMessage());
            // Handle failure to publish to DLQ, possibly log or alert
        }
    }
}
