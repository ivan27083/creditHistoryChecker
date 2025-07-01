package com.userService.repository;

import com.userService.model.User;
import com.userService.model.UserDto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    UserDto findByEmail(String email);
}