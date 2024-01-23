package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@RestController
@Slf4j
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService service;

    @GetMapping
    public List<UserDto> getAll() {
        log.info("Запрос на получение всех пользователей");
        List<UserDto> dtoList = service.getAll();
        log.info("Список всех пользователей отправлен");
        return dtoList;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto saveUser(@RequestBody UserDto user) {
        log.info("Запрос на сохранение пользователя");
        UserDto dto = service.saveUser(user);
        log.info("Пользователь сохранен, id - " + dto.getId());
        return dto;
    }

    @GetMapping("/{userId}")
    public UserDto getById(@PathVariable int userId) {
        log.info("Запрос на получение пользователя - " + userId);
        UserDto dto = service.getById(userId);
        log.info("Пользователь отправлен, id - " + dto.getId());
        return dto;
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@PathVariable int userId,
                              @RequestBody UserDto user) {
        log.info("Запрос на обновление пользователя - " + userId);
        UserDto dto = service.updateUser(userId, user);
        log.info("Пользователь обновлен, id - " + dto.getId());
        return dto;
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable int userId) {
        log.info("Запрос на удаление пользователя - " + userId);
        service.deleteUser(userId);
        log.info("Пользователь удален, id - " + userId);
    }
}
