package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService service;

    @GetMapping
    public List<UserDto> getAll() {
        return service.getAll();
    }

    @PostMapping
    public UserDto saveUser(@Valid @RequestBody User user) {
        return service.saveUser(user);
    }

    @GetMapping("/{userId}")
    public UserDto getById(@PathVariable Integer userId) {
        return service.getById(userId);
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@PathVariable Integer userId,
                              @RequestBody User user) {
        return service.updateUser(userId, user);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Integer userId) {
        service.deleteUser(userId);
    }
}
