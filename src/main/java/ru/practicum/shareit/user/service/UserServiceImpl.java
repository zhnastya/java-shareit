package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.user.mapper.UserMapper.mapToDto;
import static ru.practicum.shareit.user.mapper.UserMapper.mapToModel;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Override
    public UserDto saveUser(UserDto user) {
        User user1 = mapToModel(user);
        repository.saveUser(user1);
        return mapToDto(user1);
    }

    @Override
    public UserDto updateUser(int id, UserDto userDto) {
        return mapToDto(repository.updateUser(id, mapToModel(userDto)));
    }

    @Override
    public UserDto getById(int id) {
        User userFromMap = repository.getById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        return mapToDto(userFromMap);
    }

    @Override
    public void deleteUser(int id) {
        repository.getById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        repository.deleteUser(id);
    }

    @Override
    public List<UserDto> getAll() {
        return repository.getAll().stream()
                .map(UserMapper::mapToDto)
                .collect(Collectors.toList());
    }
}
