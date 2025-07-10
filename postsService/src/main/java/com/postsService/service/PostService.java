package com.postsService.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.postsService.model.Post;
import com.postsService.model.PostDto;
import com.postsService.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class PostService {

    private final PostRepository postRepository;
    private final ObjectMapper objectMapper;
    private final PostMapper postMapper;

    public void delete(Post post) {
        postRepository.delete(post);
    }

    public List<Post> findAll() {
        return postRepository.findAll();
    }

    public Post save(Post post) {
        return postRepository.save(post);
    }

    public Page<Post> getAll(Pageable pageable) {
        return postRepository.findAll(pageable);
    }

    public Post getOne(Integer id) {
        Optional<Post> postOptional = postRepository.findById(id);
        return postOptional.orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity with id `%s` not found".formatted(id)));
    }

    public List<Post> getMany(List<Integer> ids) {
        return postRepository.findAllById(ids);
    }

    @Transactional
    public Post create(Post post) {
        return postRepository.save(post);
    }

    public Post patch(Integer id, JsonNode patchNode) throws IOException {
        Post post = postRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity with id `%s` not found".formatted(id)));

        objectMapper.readerForUpdating(post).readValue(patchNode);

        return postRepository.save(post);
    }

    public List<Integer> patchMany(List<Integer> ids, JsonNode patchNode) throws IOException {
        Collection<Post> posts = postRepository.findAllById(ids);

        for (Post post : posts) {
            objectMapper.readerForUpdating(post).readValue(patchNode);
        }

        List<Post> resultPosts = postRepository.saveAll(posts);
        return resultPosts.stream()
                .map(Post::getId)
                .toList();
    }

    public Post delete1(Integer id) {
        Post post = postRepository.findById(id).orElse(null);
        if (post != null) {
            postRepository.delete(post);
        }
        return post;
    }

    public void deleteMany(List<Integer> ids) {
        postRepository.deleteAllById(ids);
    }

    public List<PostDto> findByUserId(Integer userId) {
        List<Post> posts = postRepository.findByUserId(userId);
        return posts.stream()
                .map(postMapper::toPostDto)
                .toList();
    }
}
