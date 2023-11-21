package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepo {
    void saveUser(User user);

    Optional<User> getById(int id);

    void deleteUser(int id);

    List<User> getAll();

    User updateUser(User user);
}
