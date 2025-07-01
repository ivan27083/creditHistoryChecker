package com.userService.model;

import lombok.Value;

import java.time.LocalDate;

/**
 * DTO for {@link User}
 */
@Value
public class UserDto {
    Integer id;
    String username;
    LocalDate created_at;
}