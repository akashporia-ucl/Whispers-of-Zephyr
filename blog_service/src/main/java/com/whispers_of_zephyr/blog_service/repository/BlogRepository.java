package com.whispers_of_zephyr.blog_service.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.whispers_of_zephyr.blog_service.model.Blog;

@Repository
public interface BlogRepository extends JpaRepository<Blog, UUID> {

}
