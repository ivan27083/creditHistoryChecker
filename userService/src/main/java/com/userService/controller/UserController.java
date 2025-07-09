package com.userService.controller;


import com.userService.model.User;
import com.userService.model.UserDto;
import com.userService.repository.UserRepository;
import com.userService.service.UserService;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(401).build();
        }

        String email = authentication.getName();
        log.info("Запрошен текущий пользователь по email: {}", email);

        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        UserDto dto = UserDto.from(optionalUser.get());
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getById(@PathVariable Integer id) {
        return userRepository.findById(id)
                .map(user -> ResponseEntity.ok(UserDto.from(user)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/by-email")
    public ResponseEntity<UserDto> getByEmail(@RequestParam @Email String email) {
        return userRepository.findByEmail(email)
                .map(user -> ResponseEntity.ok(UserDto.from(user)))
                .orElse(ResponseEntity.notFound().build());
    }
}