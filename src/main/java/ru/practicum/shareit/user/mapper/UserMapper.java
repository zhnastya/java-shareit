package ru.practicum.shareit.user.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

@Component
public class UserMapper {

    public static UserDto mapToDto(Integer id, User user) {
        return UserDto.builder()
                .id(id)
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public static User mapToModel(UserDto user) {
        return User.builder()
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }
}
