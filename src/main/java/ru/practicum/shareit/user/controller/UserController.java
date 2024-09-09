package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserDto>> getAll() {
        log.info("GET /users");
        return ResponseEntity.ok().body(userService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable("id") long userId) {
        log.info("GET /users/{} ", userId);
        return ResponseEntity.ok().body(userService.getUser(userId));
    }

    @PostMapping
    public ResponseEntity<UserDto> create(@RequestBody UserDto userDto) {
        log.info("POST /users");
        return ResponseEntity.ok().body(userService.create(userDto));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserDto> update(@PathVariable("id") long userId, @RequestBody UserDto userDto) {
        log.info("PATCH /users/{}", userId);
        return ResponseEntity.ok().body(userService.update(userId, userDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<UserDto> deleteUser(@PathVariable("id") long userId) {
        log.info("DELETE /users/{}", userId);
        userService.deleteUser(userId);
        return ResponseEntity.ok().build();
    }
}
