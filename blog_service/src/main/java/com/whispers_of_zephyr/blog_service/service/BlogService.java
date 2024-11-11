package com.whispers_of_zephyr.blog_service.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.whispers_of_zephyr.blog_service.client.CommentClient;
import com.whispers_of_zephyr.blog_service.component.BlogEvent;
import com.whispers_of_zephyr.blog_service.component.MyAppConfigComponent;
import com.whispers_of_zephyr.blog_service.dto.BlogToBlogEventDTO;
import com.whispers_of_zephyr.blog_service.model.Blog;
import com.whispers_of_zephyr.blog_service.repository.BlogRepository;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class BlogService {

    @Autowired
    private final BlogRepository blogRepository;

    @Autowired
    private final RabbitTemplate rabbitTemplate;

    @Autowired
    private final MyAppConfigComponent appConfigComponent;

    @Autowired
    private final CommentClient commentClient;

    @Autowired
    private final BlogToBlogEventDTO blogToBlogEventDTO;

    public BlogService(BlogRepository blogRepository, MyAppConfigComponent appConfigComponent,
            CommentClient commentClient, RabbitTemplate rabbitTemplate, BlogToBlogEventDTO blogToBlogEventDTO) {
        this.blogRepository = blogRepository;
        this.appConfigComponent = appConfigComponent;
        this.commentClient = commentClient;
        this.rabbitTemplate = rabbitTemplate;
        this.blogToBlogEventDTO = blogToBlogEventDTO;
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

    public Blog postBlog(Blog blog, MultipartFile image) throws IOException {
        try {
            byte[] imageBytes;
            if (image != null && !image.isEmpty()) {
                log.info("Image is not empty");
                imageBytes = image.getBytes();
            } else {
                log.info("Image is empty, using default image");
                // Get the default image from the resources folder
                InputStream imageStream = new ClassPathResource(appConfigComponent.getDefaultImagePath())
                        .getInputStream();
                log.info("Default imaged fetched from the resources folder");
                imageBytes = imageStream.readAllBytes();
            }

            // Set the image bytes to the blog object
            blog.setImage(imageBytes);

            // Log the blog details for debugging
            log.info("Blog details: {}", blog);

            // Save the blog to the repository
            Blog created = blogRepository.save(blog);

            // Publish the blog event to the RabbitMQ
            publishBlogEvent(created);

            return created;
        } catch (IOException e) {
            log.error("IO Error occurred while saving the blog: {}", e.getMessage());
            return null;
        } catch (RuntimeException e) {
            log.error("Runtime Error occurred while saving the blog: {}", e.getMessage());
            return null;
        }

    }

    private void publishBlogEvent(Blog blog) {
        log.info("Publishing the blog event to the RabbitMQ");
        BlogEvent event = blogToBlogEventDTO.toBlogEvent(blog);
        rabbitTemplate.convertAndSend("blogExchange", "blog.created", event);
    }

}
