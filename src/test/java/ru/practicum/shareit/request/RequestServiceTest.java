package ru.practicum.shareit.request;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class RequestServiceTest {

    @Autowired
    private ItemRequestService requestService;

    @Autowired
    private UserService userService;

    private final UserDto userDto = UserDto.builder()
            .name("name")
            .email("email@email.com")
            .build();

    private final ItemRequestDto requestDto = ItemRequestDto.builder()
            .description("request description")
            .build();

    @Test
    void addNewRequest() {
        UserDto addedUser = userService.saveUser(userDto);
        requestService.create(addedUser.getId(), requestDto);

        List<ItemRequestDto> actualRequests = requestService.getAllByUser(addedUser.getId());

        assertEquals("request description", actualRequests.get(0).getDescription());
    }

    @Test
    void getRequestByIdWhenRequestIdIsNotValidShouldThrowObjectNotFoundException() {
        int requestId = 2;

        Assertions
                .assertThrows(RuntimeException.class,
                        () -> requestService.getById(userDto.getId(), requestId));
    }
}
