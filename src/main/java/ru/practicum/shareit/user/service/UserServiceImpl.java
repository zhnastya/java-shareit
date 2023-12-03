package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
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
        repository.save(user1);
        return mapToDto(user1);
    }

    @Override
    public UserDto updateUser(int id, UserDto userDto) {
        try {
            User user = repository.getById(id);
            if (userDto.getEmail() != null) user.setEmail(userDto.getEmail());
            if (userDto.getName() != null) user.setName(userDto.getName());
            repository.save(user);
            return mapToDto(user);
        } catch (EntityNotFoundException e) {
            throw new NotFoundException("Пользователь не найден");
        }
    }

    @Override
    public UserDto getById(int id) {
        try {
            return mapToDto(repository.getById(id));
        } catch (EntityNotFoundException e) {
            throw new NotFoundException("Пользователь не найден");
        }
    }

    @Override
    public Optional<User> getByIdModel(int id) {
        return repository.findById(id);
    }

    @Override
    public void deleteUser(int id) {
        try {
            repository.deleteById(id);
        } catch (EntityNotFoundException e) {
            throw new NotFoundException("Пользователь не найден");
        }
    }

    @Override
    public List<UserDto> getAll() {
        return repository.findAll().stream()
                .map(UserMapper::mapToDto)
                .collect(Collectors.toList());
    }
}
