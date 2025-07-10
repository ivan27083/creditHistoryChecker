package com.userService.model;

import lombok.Value;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

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
    private final Set<Integer> subscribedToIds;
    private final Set<Integer> subscriberIds;

    public static UserDto from(User user) {
        return new UserDto(
                user.getId(),
                user.getUsername(),
                user.getCreated_at(),
                user.getSubscribedTo().stream().map(User::getId).collect(Collectors.toSet()),
                user.getSubscribers().stream().map(User::getId).collect(Collectors.toSet())
        );
    }
}