package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto saveUser(UserDto user);

    UserDto updateUser(int id, UserDto user);

    UserDto getById(int id);

    void deleteUser(int id);

    List<UserDto> getAll();
}
