package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.enums.SortField;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.item.dto.ItemFullDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class BookingServiceTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserService userService;

    @Autowired
    private ItemService itemService;

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

    private final BookingRequestDto bookingDto1 = BookingRequestDto.builder()
            .itemId(2)
            .start(LocalDateTime.now().plusSeconds(10L))
            .end(LocalDateTime.now().plusSeconds(11L))
            .build();
    private final BookingRequestDto bookingDto2 = BookingRequestDto.builder()
            .itemId(1)
            .start(LocalDateTime.now().plusSeconds(10L))
            .end(LocalDateTime.now().plusSeconds(11L))
            .build();

    @Test
    void addBooking() {
        UserDto addedUser1 = userService.saveUser(userDto1);
        UserDto addedUser2 = userService.saveUser(userDto2);
        itemService.createItem(addedUser1.getId(), itemDto1);
        itemService.createItem(addedUser2.getId(), itemDto2);

        BookingDto bookingDtoOut1 = bookingService.saveBooking(addedUser1.getId(), bookingDto1);
        BookingDto bookingDtoOut2 = bookingService.saveBooking(addedUser2.getId(), bookingDto2);

        assertEquals(1, bookingDtoOut1.getId());
        assertEquals(2, bookingDtoOut2.getId());
        assertEquals(Status.WAITING, bookingDtoOut1.getStatus());
        assertEquals(Status.WAITING, bookingDtoOut2.getStatus());

        BookingDto updatedBookingDto1 = bookingService.updateStatus(addedUser2.getId(),
                bookingDtoOut1.getId(), true);
        BookingDto updatedBookingDto2 = bookingService.updateStatus(addedUser1.getId(),
                bookingDtoOut2.getId(), true);

        assertEquals(Status.APPROVED, updatedBookingDto1.getStatus());
        assertEquals(Status.APPROVED, updatedBookingDto2.getStatus());

        List<BookingDto> bookingsDtoOut = bookingService.getSortedByOwner(addedUser2.getId(),
                SortField.ALL, PageRequest.of(0, 10));

        assertEquals(1, bookingsDtoOut.size());
    }

    @Test
    void update_whenBookingIdAndUserIdIsNotValid_thenThrowObjectNotFoundException() {
        int userId = 3;
        int bookingId = 3;

        Assertions
                .assertThrows(NotFoundException.class,
                        () -> bookingService.updateStatus(userId, bookingId, true));
    }
}
