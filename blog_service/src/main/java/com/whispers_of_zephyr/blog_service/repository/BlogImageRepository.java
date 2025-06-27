package com.whispers_of_zephyr.blog_service.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.whispers_of_zephyr.blog_service.model.BlogImage;

@Repository
public interface BlogImageRepository extends JpaRepository<BlogImage, UUID> {

    @Query("SELECT b FROM BlogImage b WHERE b.blogId = :blogId")
    Optional<BlogImage> findByBlogId(UUID blogId);
}
