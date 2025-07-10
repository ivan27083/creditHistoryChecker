package com.centralService.client;

import com.centralService.model.ImgBBResponse;
import com.centralService.model.PagedModel;
import com.centralService.model.PostDto;
import com.centralService.util.MultipartInputStreamFileResource;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PostServiceRestClient {

    private final RestTemplate restTemplate;

    @Value("${post-service.url}")
    private String postServiceUrl;

    public PagedModel<PostDto> getAllPaged(int page, int size) {
        String url = postServiceUrl + "/posts?page=" + page + "&size=" + size;
        ResponseEntity<PagedModel<PostDto>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );
        return response.getBody();
    }

    public List<PostDto> getPostsByUserId(Integer userId) {
        String url = postServiceUrl + "/posts/user/" + userId;
        ResponseEntity<List<PostDto>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );
        return response.getBody();
    }

    public PostDto getOne(Integer postId) {
        String url = postServiceUrl + "/posts/" + postId;
        return restTemplate.getForObject(url, PostDto.class);
    }

    public List<PostDto> getMany(List<Integer> ids) {
        String joined = ids.stream().map(String::valueOf).collect(Collectors.joining(","));
        String url = postServiceUrl + "/posts/by-ids?ids=" + joined;
        ResponseEntity<List<PostDto>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );
        return response.getBody();
    }

    public PostDto createPost(PostDto postDto) {
        String url = postServiceUrl + "/posts";
        return restTemplate.postForObject(url, postDto, PostDto.class);
    }

    public PostDto createPostWithImages(PostDto postDto, List<MultipartFile> files) throws IOException {
        String url = postServiceUrl + "/posts";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("post", postDtoAsJson(postDto));

        for (MultipartFile file : files) {
            body.add("images", new MultipartInputStreamFileResource(file.getInputStream(), file.getOriginalFilename()));
        }

        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);
        ResponseEntity<PostDto> response = restTemplate.postForEntity(url, request, PostDto.class);

        return response.getBody();
    }

    public PostDto updatePostWithImages(PostDto postDto, List<MultipartFile> newImages, String token) throws IOException {
        String url = postServiceUrl + "/posts/" + postDto.getId();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.setBearerAuth(token);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("post", postDtoAsJson(postDto));

        for (MultipartFile file : newImages) {
            if (!file.isEmpty()) {
                body.add("images", new MultipartInputStreamFileResource(file.getInputStream(), file.getOriginalFilename()));
            }
        }

        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);
        ResponseEntity<PostDto> response = restTemplate.exchange(url, HttpMethod.PUT, request, PostDto.class);

        return response.getBody();
    }

    public void deletePost(Integer id) {
        String url = postServiceUrl + "/posts/" + id;
        restTemplate.delete(url);
    }

    public void deleteImagesByPostId(Integer postId, String token) {
        String url = postServiceUrl + "/posts/" + postId + "/images";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        restTemplate.exchange(url, HttpMethod.DELETE, request, Void.class);
    }

    public void deleteMany(List<Integer> ids) {
        String joined = ids.stream().map(String::valueOf).collect(Collectors.joining(","));
        String url = postServiceUrl + "/posts?ids=" + joined;
        restTemplate.delete(url);
    }

    public boolean isImageAvailable(String imageUrl) {
        String url = postServiceUrl + "/posts/imageCheck";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(Map.of("imageUrl", imageUrl), headers);
        ResponseEntity<Boolean> response = restTemplate.postForEntity(url, request, Boolean.class);

        return Boolean.TRUE.equals(response.getBody());
    }

    public ImgBBResponse uploadImage(MultipartFile file) throws IOException {
        String url = postServiceUrl + "/posts/upload";

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new MultipartInputStreamFileResource(file.getInputStream(), file.getOriginalFilename()));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);
        ResponseEntity<ImgBBResponse> response = restTemplate.postForEntity(url, request, ImgBBResponse.class);

        return response.getBody();
    }

    private HttpEntity<String> postDtoAsJson(PostDto postDto) throws IOException {
        HttpHeaders jsonHeaders = new HttpHeaders();
        jsonHeaders.setContentType(MediaType.APPLICATION_JSON);
        String json = new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(postDto);
        return new HttpEntity<>(json, jsonHeaders);
    }
}
