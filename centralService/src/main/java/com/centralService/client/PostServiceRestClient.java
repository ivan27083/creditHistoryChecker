package com.centralService.client;

import com.centralService.model.ImgBBResponse;
import com.centralService.model.PagedModel;
import com.centralService.model.PostDto;
import com.centralService.util.MultipartInputStreamFileResource;
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

@Component
@RequiredArgsConstructor
public class PostServiceRestClient {

    private final RestTemplate restTemplate;

    @Value("${post-service.url}")
    private String postServiceUrl;

    public List<PostDto> getAllPosts() {
        ResponseEntity<PagedModel<PostDto>> response =
                restTemplate.exchange(
                        postServiceUrl + "/posts",
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<PagedModel<PostDto>>() {}
                );
        return response.getBody().getContent();
    }

    public List<PostDto> getPostsByUserId(Integer userId) {
        // Предположим, что такой эндпоинт добавлен на стороне PostService
        String url = postServiceUrl + "/posts/user/" + userId;
        ResponseEntity<List<PostDto>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<PostDto>>() {}
        );
        return response.getBody();
    }

    public void createPost(PostDto post, String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<PostDto> request = new HttpEntity<>(post, headers);

        restTemplate.postForEntity(postServiceUrl + "/posts", request, Void.class);
    }

    public ImgBBResponse uploadImage(MultipartFile file, String token) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new MultipartInputStreamFileResource(file.getInputStream(), file.getOriginalFilename()));

        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);

        ResponseEntity<ImgBBResponse> response = restTemplate.exchange(
                postServiceUrl + "/posts/upload",
                HttpMethod.POST,
                request,
                ImgBBResponse.class
        );

        return response.getBody();
    }

    public void deletePost(Integer postId) {
        restTemplate.delete(postServiceUrl + "/posts/" + postId);
    }
}
