package com.centralService.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDate;
import java.util.Set;

@AllArgsConstructor
@Getter
public class UserDto {
    @NotNull
    private final Integer id;
    private final String username;
    @PastOrPresent
    private final LocalDate created_at;
    private final Set<Integer> subscribedToIds;
    private final Set<Integer> subscriberIds;
}