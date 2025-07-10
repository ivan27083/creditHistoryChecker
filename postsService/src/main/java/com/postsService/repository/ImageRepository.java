package com.postsService.repository;

import com.postsService.model.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ImageRepository extends JpaRepository<Image, Integer> {
    List<Image> findByPostId(Integer postId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Image i WHERE i.post.id = :postId")
    void deleteByPostId(@Param("postId") Integer postId);
}