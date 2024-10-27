package com.whispers_of_zephyr.comment_service.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.whispers_of_zephyr.comment_service.model.Comment;

@Repository
public interface CommentRepository extends JpaRepository<Comment, UUID> {
    public List<Comment> findByBlogId(UUID blogId);
}
