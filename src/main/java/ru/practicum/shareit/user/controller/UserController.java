package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService service;

    @GetMapping
    public List<UserDto> getAll() {
        return service.getAll();
    }

    @PostMapping
    public UserDto saveUser(@Valid @RequestBody UserDto user) {
        log.info("Запрос на сохранение пользователя");
        return service.saveUser(user);
    }

    @GetMapping("/{userId}")
    public UserDto getById(@PathVariable int userId) {
        log.info("Запрос на получение пользователя - " + userId);
        return service.getById(userId);
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@PathVariable int userId,
                              @RequestBody UserDto user) {
        log.info("Запрос на обновление пользователя - " + userId);
        return service.updateUser(userId, user);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable int userId) {
        log.info("Запрос на удаление пользователя - " + userId);
        service.deleteUser(userId);
    }
}
