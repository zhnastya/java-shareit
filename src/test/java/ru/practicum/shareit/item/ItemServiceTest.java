package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemFullDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ItemServiceTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserService userService;

    @Autowired
    private ItemRequestService requestService;

    @Autowired
    private BookingService bookingService;
    private final UserDto userDto1 = UserDto.builder()
            .name("name1")
            .email("email1@email.com")
            .build();

    private final UserDto userDto2 = UserDto.builder()
            .name("name2")
            .email("email2@email.com")
            .build();

    private final ItemFullDto itemDto1 = ItemFullDto.builder()
            .name("item1 name")
            .description("item1 description")
            .available(true)
            .build();

    private final ItemFullDto itemDto2 = ItemFullDto.builder()
            .name("item2 name")
            .description("item2 description")
            .available(true)
            .build();

    private final ItemFullDto itemDtoRequest = ItemFullDto.builder()
            .name("itemDtoRequest name")
            .description("itemDtoRequest description")
            .available(true)
            .requestId(1)
            .build();

    private final ItemRequestDto requestDto = ItemRequestDto.builder()
            .description("request description")
            .build();

    private final BookingRequestDto bookingDto = BookingRequestDto.builder()
            .itemId(1)
            .start(LocalDateTime.now())
            .end(LocalDateTime.now().plusSeconds(1L))
            .build();

    private final CommentDto commentDto = CommentDto.builder()
            .text("comment text")
            .build();

    @Test
    void addCommentItem() throws InterruptedException {
        UserDto addedUser1 = userService.saveUser(userDto1);
        UserDto addedUser2 = userService.saveUser(userDto2);
        ItemFullDto addedItem = itemService.createItem(addedUser2.getId(), itemDto2);
        BookingDto bookingDtoOut = bookingService.saveBooking(addedUser1.getId(), bookingDto);

        bookingService.updateStatus(bookingDtoOut.getId(), addedUser2.getId(), true);
        Thread.sleep(2000);
        CommentDto addedComment = itemService.saveComment(addedUser1.getId(), addedItem.getId(), commentDto);

        assertEquals(1, addedComment.getId());
        assertEquals("comment text", addedComment.getText());
    }

    @Test
    void addNewItem() {
        UserDto addedUser = userService.saveUser(userDto1);
        ItemFullDto addedItem = itemService.createItem(addedUser.getId(), itemDto1);

        assertEquals(1, addedItem.getId());
        assertEquals("item1 name", addedItem.getName());
    }

    @Test
    void addRequestItem() {
        UserDto addedUser = userService.saveUser(userDto1);
        requestService.create(addedUser.getId(), requestDto);

        ItemFullDto addedItemRequest = itemService.createItem(addedUser.getId(), itemDtoRequest);

        assertEquals(1, addedItemRequest.getRequestId());
        assertEquals("itemDtoRequest name", addedItemRequest.getName());
    }

    @Test
    void getItemByIdWhenItemIdIsNotValid() {
        int itemId = 3;

        Assertions
                .assertThrows(RuntimeException.class,
                        () -> itemService.getByItemId(userDto1.getId(), itemId));
    }
}
