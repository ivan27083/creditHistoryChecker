package com.postsService.model;

import lombok.Value;

import java.time.LocalDate;


@Value
public class UserDto {
    Integer id;
    String username;
    LocalDate created_at;
}