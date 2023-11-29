package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.model.ConflictException;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Repository
public class UserRepositoryImpl implements UserRepository {
    private final Map<Integer, User> userMap = new HashMap<>();
    private final Set<String> emails = new HashSet<>();
    private int id = 1;


    private boolean checkUniqueEmail(int userId, String email) {
        if (emails.add(email)) {
            return true;
        }
        return userMap.values().stream()
                .filter(x -> x.getEmail().equals(email)
                        && !x.getId().equals(userId))
                .findFirst()
                .isEmpty();
    }

    @Override
    public void saveUser(User user) {
        if (!checkUniqueEmail(0, user.getEmail())) {
            throw new ConflictException("Пользователь с таким email уже существует");
        }
        int newId = id++;
        user.setId(newId);
        userMap.put(newId, user);
    }

    @Override
    public User updateUser(int id, User user) {
        if (!userMap.containsKey(id)) {
            throw new NotFoundException("Пользователь с id - " + id + " не найден");
        }
        if (!checkUniqueEmail(id, user.getEmail())) {
            throw new ConflictException("Пользователь с таким email уже существует");
        }
        User userFromMap = userMap.get(id);
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
        User user = userMap.remove(id);
        emails.remove(user.getEmail());
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(userMap.values());
    }
}
