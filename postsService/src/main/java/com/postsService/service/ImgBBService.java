package com.postsService.service;

import com.postsService.model.ImgBBResponse;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;

@Service
public class ImgBBService {
    private static final String API_KEY = "bed8f48265b41a7f5d1e3d7878e9a70e";
    private static final String UPLOAD_URL = "https://api.imgbb.com/1/upload";

    private final RestTemplate restTemplate = new RestTemplate();

    public ImgBBResponse uploadImage(MultipartFile file){
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
        if (deleteUrl == null || deleteUrl.isBlank()) {
            throw new IllegalArgumentException("Delete URL не может быть пустым");
        }

        try {
            URI uri = URI.create(deleteUrl);
            String[] parts = uri.getPath().split("/");
            if (parts.length < 3) {
                throw new IllegalArgumentException("Неверный формат deleteUrl");
            }
            String imageId = parts[1];
            String imageHash = parts[2];

            String url = "https://ibb.co/json";

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("pathname", "/" + imageId + "/" + imageHash);
            body.add("action", "delete");
            body.add("delete", "image");
            body.add("from", "resource");
            body.add("deleting[id]", imageId);
            body.add("deleting[hash]", imageHash);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                System.out.println("Изображение успешно удалено с ImgBB");
            } else {
                throw new RuntimeException("Ошибка удаления изображения: HTTP " + response.getStatusCodeValue());
            }

        } catch (Exception e) {
            throw new RuntimeException("Ошибка при удалении изображения с ImgBB: " + e.getMessage(), e);
        }
    }
}
