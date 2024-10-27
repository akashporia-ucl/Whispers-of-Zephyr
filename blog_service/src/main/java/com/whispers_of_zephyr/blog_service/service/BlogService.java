package com.whispers_of_zephyr.blog_service.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.whispers_of_zephyr.blog_service.client.CommentClient;
import com.whispers_of_zephyr.blog_service.component.MyAppConfigComponent;
import com.whispers_of_zephyr.blog_service.model.Blog;
import com.whispers_of_zephyr.blog_service.repository.BlogRepository;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class BlogService {

    @Autowired
    private final BlogRepository blogRepository;

    @Autowired
    private final MyAppConfigComponent appConfigComponent;

    @Autowired
    private final CommentClient commentClient;

    public BlogService(BlogRepository blogRepository, MyAppConfigComponent appConfigComponent,
            CommentClient commentClient) {
        this.blogRepository = blogRepository;
        this.appConfigComponent = appConfigComponent;
        this.commentClient = commentClient;
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
        byte[] imageBytes;
        if (image != null && !image.isEmpty()) {
            log.info("Image is not empty");
            imageBytes = image.getBytes();
        } else {
            log.info("Image is empty, using default image");
            // Get the default image from the resources folder
            InputStream imageStream = new ClassPathResource(appConfigComponent.getDefaultImagePath()).getInputStream();
            log.info("Default imaged fetched from the resources folder");
            imageBytes = imageStream.readAllBytes();
        }

        // Set the image bytes to the blog object
        blog.setImage(imageBytes);

        // Log the blog details for debugging
        log.info("Blog details: {}", blog);

        // Save the blog to the repository
        return blogRepository.save(blog);
    }

}
