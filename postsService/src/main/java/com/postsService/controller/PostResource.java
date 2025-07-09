package com.postsService.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.postsService.model.ImgBBResponse;
import com.postsService.model.Post;
import com.postsService.service.ImgBBService;
import com.postsService.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostResource {

    private final PostService postService;

    private final ImgBBService imgBBService;

    @GetMapping
    public PagedModel<Post> getAll(Pageable pageable) {
        Page<Post> posts = postService.getAll(pageable);
        return new PagedModel<>(posts);
    }

    @GetMapping("/{id}")
    public Post getOne(@PathVariable Integer id) {
        return postService.getOne(id);
    }

    @GetMapping("/by-ids")
    public List<Post> getMany(@RequestParam List<Integer> ids) {
        return postService.getMany(ids);
    }

    @PostMapping
    public Post create(@RequestBody Post post) {
        return postService.create(post);
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
    public Post delete1(@PathVariable Integer id) {
        return postService.delete1(id);
    }

    @DeleteMapping
    public void deleteMany(@RequestParam List<Integer> ids) {
        postService.deleteMany(ids);
    }

    @DeleteMapping("/{deleteUrl}")
    public void deleteImage(@PathVariable String deleteUrl) {
        imgBBService.deleteImage(deleteUrl);
    }

    @PostMapping("/imageCheck")
    public boolean isImageAvailable(@RequestBody Map<String, String> map) {
        return imgBBService.isImageAvailable(map.get("imageUrl"));
    }

    @PostMapping("/upload")
    public ImgBBResponse uploadImage(@RequestParam MultipartFile file) throws IOException {
        return imgBBService.uploadImage(file);
    }
}
