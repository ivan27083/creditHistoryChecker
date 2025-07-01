package com.postsService.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "post")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "title")
    private String title;

    @ElementCollection
    @CollectionTable(
            name = "post_images",
            joinColumns = @JoinColumn(name = "post_id")
    )
    @Column(name = "image_url", length = 2048) // Ссылки на изображения (ImgBB/S3)
    private List<String> imageUrls = new ArrayList<>(9);

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

}