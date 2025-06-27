package com.whispers_of_zephyr.blog_service.model;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "blog_images")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BlogImage {

    @Id
    @Column(name = "id", unique = true, nullable = false)
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.UUID)
    private UUID id;

    @Column(name = "blog_id", nullable = false)
    private UUID blogId;

    @Column(name = "image_id", nullable = true)
    private UUID imageId;

    public BlogImage(UUID blogId, UUID imageId) {
        this.blogId = blogId;
        this.imageId = imageId;
    }
}
