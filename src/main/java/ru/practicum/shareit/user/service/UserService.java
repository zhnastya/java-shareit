package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    UserDto saveUser(User user);

    UserDto updateUser(Integer id, User user);

    UserDto getById(Integer id);

    void deleteUser(Integer id);

    List<UserDto> getAll();
}
