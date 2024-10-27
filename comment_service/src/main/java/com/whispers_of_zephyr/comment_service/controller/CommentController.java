package com.whispers_of_zephyr.comment_service.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.whispers_of_zephyr.comment_service.model.Comment;
import com.whispers_of_zephyr.comment_service.service.CommentService;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RestController
@RequestMapping("/api/v1")
@AllArgsConstructor
@Log4j2
public class CommentController {

    @Autowired
    private final CommentService commentService;

    @GetMapping("/")
    public String helloComments() {
        return "Hello from Comment Service";
    }

    @GetMapping("/comments")
    public ResponseEntity<List<Comment>> getAllComments() {
        log.info("Getting all comments");
        List<Comment> commentsList = commentService.getAllComments();
        log.info("Fetched {} comments", commentsList.size());
        return ResponseEntity.status(200).body(commentsList);
    }

    @GetMapping("/comments/blog/{blogId}")
    public ResponseEntity<List<Comment>> getCommentsByBlogId(UUID blogId) {
        log.info("Getting all comments for blog with ID: {}", blogId);
        List<Comment> commentsList = commentService.getCommentsByBlogId(blogId);
        log.info("Fetched {} comments", commentsList.size());
        return ResponseEntity.status(200).body(commentsList);
    }

    @PostMapping("/comments/blog")
    public ResponseEntity<Comment> createCommentByBlogId(@RequestBody Comment comment) {
        log.info("Creating a new comment for blog with ID: {} and saving it to the repository", comment.getBlogId());
        Comment createdComment = commentService.createCommentByBlogId(comment);
        log.info("Comment created successfully");
        return ResponseEntity.status(201).body(createdComment);
    }

}