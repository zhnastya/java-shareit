package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;


    private final UserDto userDto = UserDto.builder()
            .id(1)
            .name("name")
            .email("my@email.com")
            .build();

    @Test
    void addNewUserReturnUserDto() {
        User userToSave = User.builder().id(1).name("name").email("my@email.com").build();
        when(userRepository.save(any(User.class))).thenReturn(userToSave);

        UserDto actualUserDto = userService.saveUser(userDto);

        assertEquals(userDto, actualUserDto);
    }

    @Test
    void updateUserTest() {
        User userUp = User.builder().id(1).name("name").email("my@email.com").build();
        UserDto fieldsToUpdate = new UserDto();
        fieldsToUpdate.setEmail("updated@example.com");
        fieldsToUpdate.setName("Updated User");

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(userUp));
        UserDto updatedUserDto = userService.updateUser(1, fieldsToUpdate);
        assertNotNull(updatedUserDto);
        assertEquals("Updated User", updatedUserDto.getName());
        assertEquals("updated@example.com", updatedUserDto.getEmail());
    }


    @Test
    void findUserByIdWhenUserFound() {
        int userId = 1;
        User expectedUser = User.builder().id(1).name("name").email("my@email.com").build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(expectedUser));
        UserDto expectedUserDto = UserMapper.mapToDto(expectedUser);

        UserDto actualUserDto = userService.getById(userId);

        assertEquals(expectedUserDto, actualUserDto);
    }

    @Test
    void findUserByIdWhenUserNotFound() {
        int userId = 0;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        NotFoundException userNotFoundException = assertThrows(NotFoundException.class,
                () -> userService.getById(userId));

        assertEquals(userNotFoundException.getMessage(), "Пользователь id - 0 не найден");
    }

    @Test
    void findAllUsersTest() {
        List<User> expectedUsers = List.of(new User());
        List<UserDto> expectedUserDto = expectedUsers
                .stream()
                .map(UserMapper::mapToDto)
                .collect(Collectors.toList());

        when(userRepository.findAll()).thenReturn(expectedUsers);

        List<UserDto> actualUsersDto = userService.getAll();

        assertEquals(actualUsersDto.size(), 1);
        assertEquals(actualUsersDto, expectedUserDto);
    }

    @Test
    void deleteUser() {
        int userId = 0;
        userService.deleteUser(userId);
        verify(userRepository, times(1)).deleteById(userId);
    }
}
