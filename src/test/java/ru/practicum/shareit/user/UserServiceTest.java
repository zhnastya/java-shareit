package ru.practicum.shareit.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserServiceTest {
    @Autowired
    private UserService userService;

    private final UserDto userDto = UserDto.builder()
            .name("name")
            .email("my@email.com")
            .build();

    @Test
    void addNewUser() {
        UserDto actualUserDto = userService.saveUser(userDto);

        assertEquals(1, actualUserDto.getId());
        assertEquals("name", actualUserDto.getName());
        assertEquals("my@email.com", actualUserDto.getEmail());
    }

    @Test
    void getUserByIdWhenUserIdIsNotValid() {
        int userId = 2;

        Assertions
                .assertThrows(NotFoundException.class, () -> userService.getById(userId));
    }
}
