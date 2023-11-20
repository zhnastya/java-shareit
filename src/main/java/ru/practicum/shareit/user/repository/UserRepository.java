package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class UserRepository {
    private final Map<Integer, User> userMap = new HashMap<>();

    public void saveUser(int id, User user) {
        userMap.put(id, user);
    }

    public Optional<User> getById(int id) {
        return userMap.containsKey(id) ? Optional.of(userMap.get(id)) : Optional.empty();
    }

    public void deleteUser(int id) {
        if (!userMap.containsKey(id)) throw new NotFoundException("Пользователь не найден");
        userMap.remove(id);
    }

    public Map<Integer, User> getAll() {
        return userMap;
    }

    public Optional<User> getByEmail(int id, String email) {
        return userMap.entrySet().stream()
                .filter(x -> x.getValue().getEmail().equals(email)
                        && x.getKey() != id)
                .map(Map.Entry::getValue)
                .findFirst();
    }
}
