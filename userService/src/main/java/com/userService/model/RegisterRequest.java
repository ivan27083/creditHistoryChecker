package com.userService.model;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * DTO for {@link User}
 */
@AllArgsConstructor
@Getter
public class RegisterRequest {
    private final String username;
    @Email
    private final String email;
    private final String password;
}