package com.whispers_of_zephyr.blog_service.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import com.whispers_of_zephyr.blog_service.client.CommentClient;
import com.whispers_of_zephyr.blog_service.component.BlogRequestToBlogEventDTO;
import com.whispers_of_zephyr.blog_service.component.MyAppConfigComponent;
import com.whispers_of_zephyr.blog_service.dto.BlogEvent;
import com.whispers_of_zephyr.blog_service.model.Blog;
import com.whispers_of_zephyr.blog_service.model.Image;
import com.whispers_of_zephyr.blog_service.repository.BlogRepository;
import com.whispers_of_zephyr.blog_service.repository.ImageRepository;

import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;

@Service
@Log4j2
public class BlogService {

    @Autowired
    private final BlogRepository blogRepository;

    @Autowired
    private final ImageRepository imageRepository;

    @Autowired
    private final RabbitTemplate rabbitTemplate;

    @Autowired
    private final MyAppConfigComponent appConfigComponent;

    @Autowired
    private final CommentClient commentClient;

    @Autowired
    private final BlogRequestToBlogEventDTO blogToBlogEventDTO;

    public BlogService(BlogRepository blogRepository, MyAppConfigComponent appConfigComponent,
            CommentClient commentClient, RabbitTemplate rabbitTemplate, BlogRequestToBlogEventDTO blogToBlogEventDTO,
            ImageRepository imageRepository) {
        this.blogRepository = blogRepository;
        this.appConfigComponent = appConfigComponent;
        this.commentClient = commentClient;
        this.rabbitTemplate = rabbitTemplate;
        this.blogToBlogEventDTO = blogToBlogEventDTO;
        this.imageRepository = imageRepository;
    }

    // @Autowired
    // private BlogRepository blogRepository; --> This is called field injection,
    // which is not recommended

    public String helloMethod() {
        log.info("Connecting to the comment service");
        return commentClient.helloComments();
    }

    public List<Blog> getBlogs() {
        log.info("Getting all blogs from the repository");
        return blogRepository.findAll();
    }

    public Blog postBlog(Blog blog, UUID imageId) throws IOException {
        try {
            // byte[] imageBytes;
            // if (image != null && !image.isEmpty()) {
            // log.info("Image is not empty");
            // imageBytes = image.getBytes();
            // } else {
            // log.info("Image is empty, using default image");
            // // Get the default image from the resources folder
            // InputStream imageStream = new
            // ClassPathResource(appConfigComponent.getDefaultImagePath())
            // .getInputStream();
            // log.info("Default imaged fetched from the resources folder");
            // imageBytes = imageStream.readAllBytes();
            // }

            // // Set the image bytes to the blog object
            // blog.setImage(imageBytes);

            // Log the blog details for debugging
            log.info("Blog details: {}", blog);

            if (imageId != null) {
                log.info("Image ID provided: {}", imageId);
                Image image = imageRepository.findById(imageId)
                        .orElseThrow(() -> new IllegalArgumentException("Image not found with id: " + imageId));
                blog.setImage(image);
            } else {
                log.warn("No Image ID provided, blog will be created without an image");
            }

            // Save the blog to the repository
            Blog created = blogRepository.save(blog);

            // Publish the blog event to the RabbitMQ
            publishBlogEvent(created);

            return created;
            // } catch (IOException e) {
            // log.error("IO Error occurred while saving the blog: {}", e.getMessage());
            // return null;
        } catch (RuntimeException e) {
            log.error("Runtime Error occurred while saving the blog: {}", e.getMessage());
            return null;
        }

    }

    private void publishBlogEvent(Blog blog) {
        log.info("Publishing the blog event to the RabbitMQ");
        BlogEvent event = new BlogEvent();
        event.setBlogTitle(blog.getTitle());
        event.setBlogContent(blog.getContent());
        event.setBlogAuthor(blog.getAuthor());
        event.setUserId(blog.getUserId());
        log.info("Blog event created: {}", event);
        rabbitTemplate.convertAndSend("notifyExchange", "notification-service.notify.updates", event);
    }

}
