package com.whispers_of_zephyr.blog_service.controller;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.whispers_of_zephyr.blog_service.model.Blog;
import com.whispers_of_zephyr.blog_service.service.BlogService;

import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RestController
@RequestMapping("/api/v1")
@NoArgsConstructor
@Log4j2
public class BlogServiceController {

    @Autowired
    private BlogService blogService;

    @GetMapping("/")
    public String helloFromService() {
        String msg = blogService.helloMethod();
        return "Hello from Blog Service and " + msg;
    }

    // Get method to get all blogs
    @GetMapping("/blogs")
    public ResponseEntity<List<Blog>> getBlogs() {
        log.info("Getting all blogs");
        List<Blog> blogs = blogService.getBlogs();
        log.info("Returning all blogs");
        return ResponseEntity.status(200).body(blogs); // OK
    }

    // Get method to get a blog by id along with the comments
    @GetMapping("/blogs/{id}")
    public ResponseEntity<String> getMethodName(@PathVariable String id) {
        return ResponseEntity.ok().body("Blog with id: " + id);
    }

    // Post method to create a blog
    @PostMapping("/blogs")
    public ResponseEntity<Blog> postBlog(@RequestParam String title, @RequestParam String content,
            @RequestParam String author, @RequestParam(required = false) MultipartFile image) throws IOException {
        log.info("Creating blog with title: " + title + ", author: " + author);
        Blog blog = new Blog(title, content, author, null);
        try {
            log.info("Calling service to create a blog");
            Blog createdBlog = blogService.postBlog(blog, image);
            log.info("Blog created successfully");
            return ResponseEntity.ok(createdBlog);
        } catch (IOException e) {
            log.error("Error while creating blog: " + e.getMessage());
            return ResponseEntity.status(500).body(blog); // Internal Server Error
        }
    }

    // Put method to update a blog
    @PutMapping("/blogs/{id}")
    public String updateBlog(@PathVariable UUID id, @RequestBody String entity) {
        // TODO: process PUT request

        return entity;
    }

    // Delete method to delete a blog
    @DeleteMapping("path/{id}")
    public String deleteBlog(@PathVariable UUID id) {
        return "Deleted blog with id: " + id;
    }

}
