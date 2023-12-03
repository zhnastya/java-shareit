package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    UserDto saveUser(UserDto user);

    UserDto updateUser(int id, UserDto userDto);

    UserDto getById(int id);

    Optional<User> getByIdModel(int id);

    void deleteUser(int id);

    List<UserDto> getAll();
}
