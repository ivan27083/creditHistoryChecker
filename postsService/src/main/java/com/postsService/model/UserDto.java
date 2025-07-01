package com.postsService.model;

import lombok.Value;

import java.time.LocalDate;


@Value
public class UserDto {
    Integer id;
    String username;
    String email;
    LocalDate created_at;
}