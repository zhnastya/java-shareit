package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    @Transactional
    @Override
    public UserDto saveUser(UserDto user) {
        User user1 = mapToModel(user);
        repository.save(user1);
        return mapToDto(user1);
    }

    @Transactional
    @Override
    public UserDto updateUser(int id, UserDto userDto) {
        User user = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь id - " + id + " не найден"));
        if (userDto.getEmail() != null) user.setEmail(userDto.getEmail());
        if (userDto.getName() != null) user.setName(userDto.getName());
        return mapToDto(user);
    }

    @Transactional(readOnly = true)
    @Override
    public UserDto getById(int id) {
        return mapToDto(repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь id - " + id + " не найден")));
    }

    @Transactional
    @Override
    public void deleteUser(int id) {
        repository.deleteById(id);
    }

    @Override
    public List<UserDto> getAll() {
        return repository.findAll().stream()
                .map(UserMapper::mapToDto)
                .collect(Collectors.toList());
    }
}
