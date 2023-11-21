package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.model.ConflictException;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Repository
public class UserRepoImpl implements UserRepo {
    private final Map<Integer, User> userMap = new HashMap<>();
    private int id = 1;


    private boolean checkUniqueEmail(User user) {
        return getByEmail(user.getId(), user.getEmail()).isPresent();
    }

    public Optional<User> getByEmail(int id, String email) {
        return userMap.values().stream()
                .filter(x -> x.getEmail().equals(email)
                        && x.getId() != id)
                .findFirst();
    }

    @Override
    public void saveUser(User user) {
        if (checkUniqueEmail(user)) {
            throw new ConflictException("Пользователь с таким email уже существует");
        }
        int newId = id++;
        user.setId(newId);
        userMap.put(newId, user);
    }

    @Override
    public User updateUser(User user) {
        if (!userMap.containsKey(user.getId())) {
            throw new NotFoundException("Пользователь с id - " + user.getId() + " не найден");
        }
        if (checkUniqueEmail(user)) {
            throw new ConflictException("Пользователь с таким email уже существует");
        }
        User userFromMap = userMap.get(user.getId());
        if (user.getEmail() != null) {
            userFromMap.setEmail(user.getEmail());
        }
        if (user.getName() != null) {
            userFromMap.setName(user.getName());
        }
        return userFromMap;
    }

    @Override
    public Optional<User> getById(int id) {
        return userMap.containsKey(id) ? Optional.of(userMap.get(id)) : Optional.empty();
    }

    @Override
    public void deleteUser(int id) {
        userMap.remove(id);
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(userMap.values());
    }
}
