package ru.practicum.shareit.user.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.exception.model.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private final Map<Integer, User> userMap = new HashMap<>();
    private Integer id = 1;


    private UserDto mapperUser(Integer id, User user) {
        return UserDto.builder()
                .id(id)
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    private boolean checkUniqueEmail(Integer id, User user) {
        Optional<User> userOptional = userMap.entrySet().stream()
                .filter(user1 -> user1.getValue().getEmail().equals(user.getEmail()) && !user1.getKey().equals(id))
                .map(Map.Entry::getValue)
                .findFirst();
        return userOptional.isPresent();
    }

    @Override
    public UserDto saveUser(User user) {
        if (checkUniqueEmail(id, user)) throw new ValidationException("Пользователь с таким email существует");
        Integer newId = id++;
        userMap.put(newId, user);
        return mapperUser(newId, user);
    }

    @Override
    public UserDto updateUser(Integer id, User user) {
        User userFromMap = userMap.get(id);
        if (userFromMap == null) throw new NotFoundException("Пользователь не найден");
        if (user.getEmail() != null && checkUniqueEmail(id, user)) {
            throw new ValidationException("Пользователь с таким email существует");
        }
        if (user.getEmail() != null) userFromMap.setEmail(user.getEmail());
        if (user.getName() != null) userFromMap.setName(user.getName());
        return mapperUser(id, userFromMap);
    }

    @Override
    public UserDto getById(Integer id) {
        User userFromMap = userMap.get(id);
        if (userFromMap == null) throw new NotFoundException("Пользователь не найден");
        return mapperUser(id, userFromMap);
    }

    @Override
    public void deleteUser(Integer id) {
        User userFromMap = userMap.get(id);
        if (userFromMap == null) throw new NotFoundException("Пользователь не найден");
        userMap.remove(id);
    }

    @Override
    public List<UserDto> getAll() {
        return userMap.entrySet().stream()
                .map(userEntry -> mapperUser(userEntry.getKey(), userEntry.getValue()))
                .collect(Collectors.toList());
    }
}
