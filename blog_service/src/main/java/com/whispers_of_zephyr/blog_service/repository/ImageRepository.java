package com.whispers_of_zephyr.blog_service.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.whispers_of_zephyr.blog_service.model.Image;

@Repository
public interface ImageRepository extends JpaRepository<Image, UUID> {

}
