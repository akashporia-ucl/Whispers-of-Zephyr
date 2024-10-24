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

@Service
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
        return blogRepository.findAll();
    }

    public Blog postBlog(Blog blog, MultipartFile image) throws IOException {
        byte[] imageBytes = null;
        blog.setImage(image.getBytes());
        if (!image.isEmpty()) {
            blog.setImage(image.getBytes());
        } else {
            imageBytes = Files.readAllBytes(Paths.get(appConfigComponent.getDefaultImagePath()));
        }
        blog.setImage(imageBytes);
        return blogRepository.save(blog);
    }
}
