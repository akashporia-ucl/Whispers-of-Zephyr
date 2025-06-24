package com.whispers_of_zephyr.blog_service.controller;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.whispers_of_zephyr.blog_service.dto.BlogRequest;
import com.whispers_of_zephyr.blog_service.model.Blog;
import com.whispers_of_zephyr.blog_service.service.BlogService;
import com.whispers_of_zephyr.blog_service.service.MinioService;
import com.whispers_of_zephyr.blog_service.service.messaging.producer.BlogMessageProducer;

import jakarta.validation.Valid;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import okhttp3.internal.ws.RealWebSocket;

@RestController
@RequestMapping("/blog-service/api/v1")
@NoArgsConstructor
@Log4j2
public class BlogServiceController {

    @Autowired
    private BlogMessageProducer messageProducer;

    @Autowired
    private BlogService blogService;

    @Autowired
    private MinioService minioService;

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
    public ResponseEntity<Boolean> postBlog(
            @ModelAttribute @Valid BlogRequest blogRequest,
            @RequestPart(value = "image", required = false) MultipartFile image,
            @RequestHeader("X-User-Id") String userId) throws IOException {

        log.info("Creating blog with title: {}, userId: {}", blogRequest.getTitle(), userId);

        try {
            UUID userUUID = UUID.fromString(userId); // Convert userId to UUID

            // Blog blog = new Blog(title, content, author, userUUID);

            log.info("Calling service to create a blog");
            // Blog createdBlog = blogService.postBlog(blog);
            UUID ImageUuid = minioService.uploadFile(image, userUUID); // Upload image to Minio
            log.info("Image uploaded successfully with image uuid: {}", ImageUuid);
            boolean messageSent = messageProducer.sendBlogCreateMessage(blogRequest, userUUID, ImageUuid);
            log.info("Blog message created successfully with image uploaded: {}", messageSent);

            return ResponseEntity.ok(messageSent);
        } catch (IllegalArgumentException e) {
            log.error("Invalid UUID format for userId: {}", userId);
            return ResponseEntity.status(400).body(null); // Return Bad Request if UUID is invalid
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
