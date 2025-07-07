package com.userService.controller;

import com.userService.model.RegisterRequest;
import com.userService.service.JwtUtil;
import com.userService.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtUtil jwtUtil;
    private final UserService userService;

    public record LoginRequest(String email, String password) {}

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        log.info("Login request: username={}", loginRequest.email());

        boolean credentialsValid = userService.checkCredentials(loginRequest.email(), loginRequest.password());
        if (!credentialsValid) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid username or password"));
        }

        List<String> roles = userService.getUserRoles(loginRequest.email());
        String token = jwtUtil.generateToken(loginRequest.email(), roles);
        return ResponseEntity.ok(Map.of("token", token));
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody RegisterRequest request) {
        log.info("Register user: username={} email={}",
                request.getUsername(), request.getEmail());

        userService.saveUser(request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/remove/{id}")
    public ResponseEntity<Void> removeUserById(@PathVariable("id") int id) {
        log.info("Remove user: id={}", id);

        userService.removeUserById(id);
        return ResponseEntity.ok().build();
    }
}
