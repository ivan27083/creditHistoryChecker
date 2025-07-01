package com.userService.service;

import com.userService.model.User;
import com.userService.model.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface UserMapper {
    UserDto toUserDto(User user);

    User toEntity(UserDto userDto);

    User updateWithNull(UserDto userDto, @MappingTarget User user);
}