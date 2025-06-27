package com.whispers_of_zephyr.blog_service.service;

import java.io.IOException;

import java.util.List;
import java.util.UUID;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import com.whispers_of_zephyr.blog_service.client.CommentClient;
import com.whispers_of_zephyr.blog_service.component.BlogRequestToBlogEventDTO;
import com.whispers_of_zephyr.blog_service.component.MyAppConfigComponent;
import com.whispers_of_zephyr.blog_service.dto.BlogEvent;
import com.whispers_of_zephyr.blog_service.dto.BlogResponse;
import com.whispers_of_zephyr.blog_service.model.Blog;
import com.whispers_of_zephyr.blog_service.model.BlogImage;
import com.whispers_of_zephyr.blog_service.model.Image;
import com.whispers_of_zephyr.blog_service.repository.BlogImageRepository;
import com.whispers_of_zephyr.blog_service.repository.BlogRepository;
import com.whispers_of_zephyr.blog_service.repository.ImageRepository;

import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class BlogService {

    @Autowired
    private final BlogRepository blogRepository;

    @Autowired
    private final ImageRepository imageRepository;

    @Autowired
    private final BlogImageRepository blogImageRepository;

    @Autowired
    private final MinioService minioService;

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
            ImageRepository imageRepository, BlogImageRepository blogImageRepository, MinioService minioService) {
        this.blogRepository = blogRepository;
        this.appConfigComponent = appConfigComponent;
        this.commentClient = commentClient;
        this.rabbitTemplate = rabbitTemplate;
        this.blogToBlogEventDTO = blogToBlogEventDTO;
        this.imageRepository = imageRepository;
        this.blogImageRepository = blogImageRepository;
        this.minioService = minioService;
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

    @Transactional
    public Blog postBlog(Blog blog, UUID imageId) throws IOException {
        try {
            log.info("Blog details: {}", blog);

            // Save the blog to the repository
            Blog created = blogRepository.save(blog);
            log.info("Blog created successfully with ID: {}", created.getId());
            BlogImage blogImage = new BlogImage(blog.getId(), imageId);
            log.info("Creating BlogImage association with Blog ID: {} and Image ID: {}", blog.getId(), imageId);

            blogImageRepository.save(blogImage);

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

    public BlogResponse getBlogById(UUID id) {
        try {
            log.info("Fetching blog with ID: {}", id);
            Blog blog = blogRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Blog not found with id: " + id));

            log.info("Blog found: {}", blog);

            // Create BlogResponse object
            BlogImage blogImage = blogImageRepository.findByBlogId(id)
                    .orElseThrow(() -> new IllegalArgumentException("BlogImage not found for blog id: " + id));
            log.info("BlogImage found with blogId {} and imageId {}", blogImage.getBlogId(), blogImage.getImageId());
            Image image = imageRepository.findById(blogImage.getImageId())
                    .orElseThrow(
                            () -> new IllegalArgumentException("Image not found with id: " +
                                    blogImage.getImageId()));
            log.info("Image found with filename: {}", image.getFileName());
            String imageURL = minioService.getFileURL(image.getFileName());
            log.info("Image URL retrieved: {}", imageURL);
            BlogResponse response = new BlogResponse(blog.getTitle(), blog.getContent(),
                    blog.getAuthor(), imageURL);
            log.info("BlogResponse created: {}", response);
            return response;
        } catch (IllegalArgumentException e) {
            log.error("Error fetching blog by ID: {}", e.getMessage());
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
