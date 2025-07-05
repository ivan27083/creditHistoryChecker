package com.userService.controller;

import com.userService.model.RegisterRequest;
import com.userService.model.User;
import com.userService.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        userService.delete(id);
    }

    @GetMapping
    @ResponseBody
    public List<User> findAll() {
        return userService.findAll();
    }

    @GetMapping("/email/{email}")
    @ResponseBody
    public User findByEmail(@PathVariable String email) {
        return userService.findByEmail(email);
    }

    @GetMapping("/id/{id}")
    @ResponseBody
    public Optional<User> findById(@PathVariable Integer id) {
        return userService.findById(id);
    }

    @PostMapping
    public User save(@RequestBody RegisterRequest request) {
        return userService.save(request);
    }
}

