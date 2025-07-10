package com.postsService.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.postsService.model.Image;
import com.postsService.model.ImgBBResponse;
import com.postsService.model.Post;
import com.postsService.model.PostDto;
import com.postsService.repository.ImageRepository;
import com.postsService.service.ImgBBService;
import com.postsService.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostResource {

    private final PostService postService;
    private final ImageRepository imageRepository;
    private final ImgBBService imgBBService;
    private final ExecutorService asyncExecutor = Executors.newFixedThreadPool(2);

    @GetMapping
    public PagedModel<Post> getAll(Pageable pageable) {
        Page<Post> posts = postService.getAll(pageable);
        return new PagedModel<>(posts);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PostDto>> getPostsByUserId(@PathVariable Integer userId) {
        List<PostDto> posts = postService.findByUserId(userId);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/{id}")
    public Post getOne(@PathVariable Integer id) {
        return postService.getOne(id);
    }

    @GetMapping("/by-ids")
    public List<Post> getMany(@RequestParam List<Integer> ids) {
        return postService.getMany(ids);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Post> createPostWithImages(
            @RequestPart("post") String postJson,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        Post post = mapper.readValue(postJson, Post.class);
        return createPost(post, images);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Post> createPost(@RequestBody Post post) throws IOException {
        return createPost(post, null);
    }

    private ResponseEntity<Post> createPost(Post post, List<MultipartFile> images) throws IOException {
        Post savedPost = postService.create(post);

        if (images != null) {
            for (MultipartFile file : images) {
                ImgBBResponse response = imgBBService.uploadImage(file);
                Image image = new Image();
                image.setUrl(response.getData().getUrl());
                image.setDelete_url(response.getData().getDelete_url());
                image.setPost(savedPost);
                imageRepository.save(image);
            }
        }

        return ResponseEntity.ok(savedPost);
    }

    @PatchMapping("/{id}")
    public Post patch(@PathVariable Integer id, @RequestBody JsonNode patchNode) throws IOException {
        return postService.patch(id, patchNode);
    }

    @PatchMapping
    public List<Integer> patchMany(@RequestParam List<Integer> ids, @RequestBody JsonNode patchNode) throws IOException {
        return postService.patchMany(ids, patchNode);
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> deletePost(@PathVariable Integer id) {
        Post post = postService.getOne(id);

        List<String> deleteUrls = post.getImages().stream()
                .map(Image::getDelete_url)
                .toList();

        asyncExecutor.execute(() -> {
            deleteUrls.forEach(url -> {
                try {
                    imgBBService.deleteImage(url);
                } catch (Exception e) {
                    log.error("Ошибка удаления изображения: {}", url, e);
                }
            });
        });

        postService.delete(post);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public void deleteMany(@RequestParam List<Integer> ids) {
        postService.deleteMany(ids);
    }

    @DeleteMapping("/url/{deleteUrl}")
    public void deleteImage(@PathVariable String deleteUrl) {
        imgBBService.deleteImage(deleteUrl);
    }

    @PostMapping("/imageCheck")
    public boolean isImageAvailable(@RequestBody Map<String, String> map) {
        return imgBBService.isImageAvailable(map.get("imageUrl"));
    }

    @PostMapping("/upload")
    public ImgBBResponse uploadImage(@RequestParam MultipartFile file) {
        return imgBBService.uploadImage(file);
    }
}
