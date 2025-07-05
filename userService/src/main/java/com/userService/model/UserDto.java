package com.userService.model;

import lombok.Value;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDate;
import java.util.Set;

/**
 * DTO for {@link User}
 */
@Value
public class UserDto {
    @NotNull
    Integer id;
    String username;
    @PastOrPresent
    LocalDate created_at;
    Set<Integer> subscribedToIds;
    Set<Integer> subscriberIds;
}