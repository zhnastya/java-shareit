package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.exception.model.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    private int id = 1;


    private UserDto mapToDto(Integer id, User user) {
        return UserDto.builder()
                .id(id)
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    private User mapToModel(UserDto user) {
        return User.builder()
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    private boolean checkUniqueEmail(int id, UserDto user) {
        return repository.getByEmail(id, user.getEmail()).isPresent();
    }

    @Override
    public UserDto saveUser(UserDto user) {
        if (checkUniqueEmail(id, user)) throw new ValidationException("Пользователь с таким email существует");
        int newId = id++;
        repository.saveUser(newId, mapToModel(user));
        User user1 = mapToModel(user);
        return mapToDto(newId, user1);
    }

    @Override
    public UserDto updateUser(int id, UserDto user) {
        User userFromMap = repository.getById(id).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        if (user.getEmail() != null && checkUniqueEmail(id, user)) {
            throw new ValidationException("Пользователь с таким email существует");
        }
        if (user.getEmail() != null) userFromMap.setEmail(user.getEmail());
        if (user.getName() != null) userFromMap.setName(user.getName());
        return mapToDto(id, userFromMap);
    }

    @Override
    public UserDto getById(int id) {
        User userFromMap = repository.getById(id).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        return mapToDto(id, userFromMap);
    }

    @Override
    public void deleteUser(int id) {
        repository.getById(id).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        repository.deleteUser(id);
    }

    @Override
    public List<UserDto> getAll() {
        return repository.getAll().entrySet().stream()
                .map(userEntry -> mapToDto(userEntry.getKey(), userEntry.getValue()))
                .collect(Collectors.toList());
    }
}
