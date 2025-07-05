package com.userService.service;

import com.userService.model.User;
import com.userService.model.RegisterRequest;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface RegisterMapper {
    User toEntity(RegisterRequest registerRequest);

    RegisterRequest toRegisterMapper(User user);
}