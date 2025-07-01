package com.userService.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.userService.model.UserDto;
import com.userService.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserResource {

    private final UserService userService;

    @GetMapping
    public List<UserDto> getAll() {
        return userService.getAll();
    }

    @GetMapping("/{id}")
    public UserDto getOne(@PathVariable Integer id) {
        return userService.getOne(id);
    }

    @GetMapping("/by-ids")
    public List<UserDto> getMany(@RequestParam List<Integer> ids) {
        return userService.getMany(ids);
    }

    @PostMapping
    public UserDto create(@RequestBody UserDto dto) {
        return userService.create(dto);
    }

    @PatchMapping("/{id}")
    public UserDto patch(@PathVariable Integer id, @RequestBody JsonNode patchNode) throws IOException {
        return userService.patch(id, patchNode);
    }

    @PatchMapping
    public List<Integer> patchMany(@RequestParam List<Integer> ids, @RequestBody JsonNode patchNode) throws IOException {
        return userService.patchMany(ids, patchNode);
    }

    @DeleteMapping("/{id}")
    public UserDto delete1(@PathVariable Integer id) {
        return userService.delete1(id);
    }

    @DeleteMapping
    public void deleteMany(@RequestParam List<Integer> ids) {
        userService.deleteMany(ids);
    }
}
