package com.postsService.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import javax.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class PostDto {
    @NotNull
    private final Integer id;
    @NotNull
    private final Integer userId;
    @NotBlank
    private final String title;
    private final String text;
    private final List<ImageDto> images;
    @PastOrPresent
    private final LocalDateTime createdAt;

    @AllArgsConstructor
    @Getter
    @EqualsAndHashCode
    public static class ImageDto {
        private final String url;
        private final String deleteUrl;
    }
}