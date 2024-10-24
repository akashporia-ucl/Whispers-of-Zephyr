package com.whispers_of_zephyr.blog_service.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.whispers_of_zephyr.blog_service.model.Blog;
import com.whispers_of_zephyr.blog_service.service.BlogService;

@RestController
@RequestMapping("/api/v1/")
public class BlogServiceController {

    @Autowired
    private BlogService blogService;

    @GetMapping("/")
    public String home() {
        return "Welcome to Blog Service!";
    }

    @GetMapping("/blogs")
    public List<Blog> getBlogs() {
        return blogService.getBlogs();
    }

    @PostMapping("/blogs")
    public ResponseEntity<Blog> postBlog(@RequestParam("title") String title, @RequestParam("content") String content,
            @RequestParam("author") String author, @RequestParam("image") MultipartFile image) throws IOException {
        Blog blog = new Blog(title, content, author, image.getBytes());
        try {
            return ResponseEntity.ok(blogService.postBlog(blog, image));
        } catch (IOException e) {
            return ResponseEntity.status(500).body(blog); // Internal Server Error
        }

    }

}
