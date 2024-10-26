package com.whispers_of_zephyr.blog_service.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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

    public BlogService(BlogRepository blogRepository, MyAppConfigComponent appConfigComponent) {
        this.blogRepository = blogRepository;
        this.appConfigComponent = appConfigComponent;
    }

    // @Autowired
    // private BlogRepository blogRepository; --> This is called field injection,
    // which is not recommended

    public List<Blog> getBlogs() {
        log.info("Getting all blogs from the repository");
        return blogRepository.findAll();
    }

    public Blog postBlog(Blog blog, MultipartFile image) throws IOException {
        byte[] imageBytes;

        if (!image.isEmpty()) {
            log.info("Image is not empty");
            imageBytes = image.getBytes();
        } else {
            log.info("Image is empty, using default image");
            imageBytes = Files.readAllBytes(Paths.get(appConfigComponent.getDefaultImagePath()));
        }

        // Set the image bytes to the blog object
        blog.setImage(imageBytes);

        // Log the blog details for debugging
        log.info("Blog details: {}", blog);

        // Save the blog to the repository
        return blogRepository.save(blog);
    }

}
