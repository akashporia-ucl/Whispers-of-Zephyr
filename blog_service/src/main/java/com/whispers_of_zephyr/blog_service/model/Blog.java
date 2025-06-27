package com.whispers_of_zephyr.blog_service.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "blogs")
@NoArgsConstructor
@AllArgsConstructor
public class Blog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "title")
    @NotBlank(message = "Title is mandatory")
    private String title;

    @Lob
    @Column(name = "content", length = 10000)
    @NotBlank(message = "Content is mandatory")
    private String content;

    @Column(name = "author")
    @NotBlank(message = "Author is mandatory")
    private String author;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "user_id")
    @NotNull(message = "User ID is mandatory")
    private UUID userId;

    public Blog(String title, String content, String author, UUID userId) {
        this.title = title;
        this.content = content;
        this.author = author;
        this.userId = userId;
    }

    // public Blog(String title, String content, String author, UUID userId, Image
    // image) {
    // this.title = title;
    // this.content = content;
    // this.author = author;
    // this.userId = userId;
    // this.image = image;
    // }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}