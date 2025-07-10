package com.centralService.model;

import lombok.*;

import javax.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
public class PostDto {
    @NotNull
    private Integer id;
    @NotNull
    private Integer userId;
    @NotBlank
    private String title;
    private String text;
    private List<ImageDto> images;
    @PastOrPresent
    private LocalDateTime createdAt;
    private String username;

    @AllArgsConstructor
    @Getter
    @EqualsAndHashCode
    public static class ImageDto {
        private final String url;
        private final String delete_url;
    }
}