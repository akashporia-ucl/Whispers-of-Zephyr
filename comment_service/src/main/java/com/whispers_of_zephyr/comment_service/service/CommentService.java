package com.whispers_of_zephyr.comment_service.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.whispers_of_zephyr.comment_service.model.Comment;
import com.whispers_of_zephyr.comment_service.repository.CommentRepository;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
@AllArgsConstructor
@Transactional
public class CommentService {

    @Autowired
    private final CommentRepository commentRepository;

    public List<Comment> getAllComments() {
        log.info("Getting all comments from the repository");
        return commentRepository.findAll();
    }

    public List<Comment> getCommentsByBlogId(UUID blogId) {
        log.info("Getting all comments for blog with ID: {}", blogId);
        return commentRepository.findByBlogId(blogId);
    }

    public Comment processCommentCreate(Comment comment) {
        log.info("Creating a new comment for blog with ID: {}", comment.getBlogId());
        return commentRepository.save(comment);
    }

    public Comment processCommentUpdate(UUID id, Comment comment) {
        log.info("Processing comment update with ID: {}", id);
        Comment commentToUpdate = commentRepository.findById(id).orElse(null);
        if (commentToUpdate == null) {
            log.error("Comment with ID: {} not found", id);
            throw new RuntimeException("Comment not found");
        }
        commentToUpdate.setContent(comment.getContent());
        return commentRepository.save(commentToUpdate);
    }

}
