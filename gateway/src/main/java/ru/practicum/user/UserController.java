package ru.practicum.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.user.dto.UserDto;

import javax.validation.Valid;

@RestController
@Slf4j
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserClient client;

    @GetMapping
    public ResponseEntity<Object> getAll() {
        log.info("Запрос на получение всех пользователей");
        return client.getAll();
    }

    @PostMapping
    public ResponseEntity<Object> saveUser(@Valid @RequestBody UserDto user) {
        log.info("Запрос на сохранение пользователя");
        return client.saveUser(user);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getById(@PathVariable int userId) {
        log.info("Запрос на получение пользователя - " + userId);
        return client.getById(userId);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@PathVariable int userId,
                                             @RequestBody UserDto user) {
        log.info("Запрос на обновление пользователя - " + userId);
        return client.updateUser(userId, user);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable int userId) {
        log.info("Запрос на удаление пользователя - " + userId);
        client.deleteUser(userId);
        log.info("Пользователь удален, id - " + userId);
    }
}
