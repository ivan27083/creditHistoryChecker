package com.postsService.service;

import com.postsService.model.ImgBBResponse;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class ImgBBService {
    private static final String API_KEY = "bed8f48265b41a7f5d1e3d7878e9a70e";
    private static final String UPLOAD_URL = "https://api.imgbb.com/1/upload";

    private final RestTemplate restTemplate;

    public ImgBBService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ImgBBResponse uploadImage(MultipartFile file) throws IOException {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("key", API_KEY);
        body.add("image", file.getResource());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        ResponseEntity<ImgBBResponse> response = restTemplate.exchange(
                UPLOAD_URL,
                HttpMethod.POST,
                new HttpEntity<>(body, headers),
                ImgBBResponse.class
        );

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            return response.getBody();
        } else {
            throw new RuntimeException("Ошибка загрузки: " + response.getStatusCode());
        }
    }

    public boolean isImageAvailable(String imageUrl) {
        try {
            ResponseEntity<Void> response = restTemplate.exchange(
                    imageUrl,
                    HttpMethod.HEAD,
                    null,
                    Void.class
            );
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            return false;
        }
    }

    public void deleteImage(String deleteUrl) {
        try {
            restTemplate.delete(deleteUrl);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка удаления изображения: " + e.getMessage());
        }
    }
}
