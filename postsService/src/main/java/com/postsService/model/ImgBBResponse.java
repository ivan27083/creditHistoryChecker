package com.postsService.model;

import lombok.*;

@Data
public class ImgBBResponse {
    private ImageData data;

    @lombok.Data
    public static class ImageData {
        private String url;
        private String delete_url;
    }
}
