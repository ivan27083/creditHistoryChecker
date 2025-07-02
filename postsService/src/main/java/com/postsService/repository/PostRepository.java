package com.postsService.repository;

import com.postsService.model.Image;
import com.postsService.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface PostRepository extends JpaRepository<Post, Integer> {
    @Transactional
    @Modifying
    @Query("DELETE FROM Image i WHERE i.post.id = :postId AND i.imageUrl = :imageUrl")
    void deleteImageFromPost(@Param("postId") Integer postId, @Param("imageUrl") String imageUrl);
}