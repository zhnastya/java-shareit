package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.exeptions.BookingException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.item.dto.BookingDtoForItem;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemFullDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    private final User user = User.builder()
            .id(1)
            .name("username")
            .email("email@email.com")
            .comments(new ArrayList<>())
            .bookings(new ArrayList<>())
            .build();

    private final User user2 = User.builder()
            .id(2)
            .name("username2")
            .email("email2@email.com")
            .build();


    private final UserDto userDto = UserDto.builder()
            .id(1)
            .name("username")
            .email("email@email.com")
            .build();

    private final Item item = Item.builder()
            .id(1)
            .name("item name")
            .description("description")
            .available(true)
            .owner(user)
            .build();

    private final ItemFullDto itemDto = ItemFullDto.builder()
            .id(1)
            .name("item name")
            .description("description")
            .available(true)
            .comments(Collections.emptyList())
            .build();

    private final Comment comment = Comment.builder()
            .id(1)
            .text("comment")
            .timeOfCreated(LocalDateTime.now())
            .author(user)
            .item(item)
            .build();

    private final Booking booking = Booking.builder()
            .id(1)
            .item(item)
            .booker(user)
            .status(Status.APPROVED)
            .start(LocalDateTime.now().minusDays(1L))
            .end(LocalDateTime.now().plusDays(1L))
            .build();

    @Test
    void addNewItemWhenInvoked() {
        Item itemSaveTest = Item.builder()
                .id(1)
                .name("test item name")
                .description("test description")
                .available(true)
                .build();

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.save(any(Item.class))).thenReturn(itemSaveTest);

        ItemFullDto actualItemDto = itemService.createItem(userDto.getId(), ItemMapper.mapperToDto(itemSaveTest));

        assertEquals(actualItemDto.getName(), "test item name");
        assertEquals(actualItemDto.getDescription(), "test description");
    }

    @Test
    void getItemById() {
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        ItemFullDto actualItemDto = itemService.getByItemId(user.getId(), item.getId());

        assertEquals(itemDto, actualItemDto);
    }

    @Test
    void getAllByUser() {
        Item itemTest = Item.builder()
                .id(1)
                .owner(user2)
                .name("test item name")
                .description("test description")
                .available(true)
                .build();

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findByOwnerIdOrderById(any(Integer.class))).thenReturn(List.of(itemTest));
        when(commentRepository.findAllByItem_IdIn(any(List.class))).thenReturn(List.of(comment));

        List<ItemFullDto> actualItemDtos = itemService.getAllByUser(user.getId());

        assertEquals(actualItemDtos.size(), 1);
        assertEquals(itemTest.getId(), actualItemDtos.get(0).getId());
        assertEquals(itemTest.getName(), actualItemDtos.get(0).getName());
        assertEquals(itemTest.getDescription(), actualItemDtos.get(0).getDescription());
    }

    @Test
    void getByName() {
        Item itemTest = Item.builder()
                .id(1)
                .owner(user2)
                .name("test item name")
                .description("test description")
                .available(true)
                .build();

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findItemByAvailableAndQueryContainWithIgnoreCase(any(String.class))).thenReturn(List.of(itemTest));
        when(commentRepository.findAllByItem_IdIn(any(List.class))).thenReturn(List.of(comment));

        List<ItemFullDto> actualItemDtos = itemService.getByName(user.getId(), "name");

        assertEquals(1, actualItemDtos.size());
        assertEquals(itemTest.getId(), actualItemDtos.get(0).getId());
        assertEquals(itemTest.getName(), actualItemDtos.get(0).getName());
        assertEquals(itemTest.getDescription(), actualItemDtos.get(0).getDescription());
    }


    @Test
    void updateItem() {
        ItemRequest itemRequest = new ItemRequest(1, "description", user, LocalDateTime.now());
        Item updatedItem = Item.builder()
                .id(1)
                .name("updated name")
                .description("updated description")
                .available(false)
                .owner(user)
                .request(itemRequest)
                .build();

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(updatedItem));

        ItemFullDto savedItem = itemService.updateItem(user.getId(), itemDto.getId(), ItemMapper.mapperToDto(updatedItem));

        assertEquals("updated name", savedItem.getName());
        assertEquals("updated description", savedItem.getDescription());
    }

    @Test
    void mapperBooking() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        BookingDtoForItem booking = ItemMapper.bookingDtoForItem(Booking.builder()
                .id(1)
                .item(item)
                .booker(user)
                .status(Status.APPROVED)
                .start(start)
                .end(end)
                .build());

        assertEquals(1, booking.getId());
        assertEquals(start, booking.getStart());
        assertEquals(end, booking.getEnd());
        assertEquals(Status.APPROVED, booking.getStatus());
    }

    @Test
    void updateItemWhenUserIsNotItemOwnerShouldThrowException() {
        Item updatedItem = Item.builder()
                .id(1)
                .name("updated name")
                .description("updated description")
                .available(false)
                .owner(user2)
                .build();

        when(itemRepository.findById(anyInt())).thenReturn(Optional.ofNullable(updatedItem));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        NotFoundException itemNotFoundException = assertThrows(NotFoundException.class,
                () -> {
                    assert updatedItem != null;
                    itemService.updateItem(user.getId(), itemDto.getId(), ItemMapper.mapperToDto(updatedItem));
                });

        assertEquals(itemNotFoundException.getMessage(), "Пользователь - 1не является владельцем вещи - 1");
    }

    @Test
    void updateItemWhenItemIdIsNotValid() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyInt())).thenReturn(Optional.empty());

        NotFoundException itemNotFoundException = assertThrows(NotFoundException.class,
                () -> itemService.updateItem(user.getId(), itemDto.getId(), ItemMapper.mapperToDto(item)));
        assertEquals(itemNotFoundException.getMessage(), "Вещь с id - 1не найдена");
    }


    @Test
    void createComment() {
        CommentDto expectedCommentDto = CommentMapper.commentToDto(comment);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(bookingRepository.findAllByBookerAndItemAndStatus(any(User.class), any(Item.class), any(Status.class), any(LocalDateTime.class)))
                .thenReturn(List.of(booking));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        CommentDto actualCommentDto = itemService.saveComment(user.getId(), item.getId(), CommentMapper.commentToDto(comment));

        assertEquals(expectedCommentDto, actualCommentDto);
    }

    @Test
    void createComment_whenItemIdIsNotValid_thenThrowObjectNotFoundException() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.empty());

        NotFoundException itemNotFoundException = assertThrows(NotFoundException.class,
                () -> itemService.saveComment(user.getId(), item.getId(), CommentMapper.commentToDto(comment)));

        assertEquals(itemNotFoundException.getMessage(), "Вещь с id - 1не найдена");
    }

    @Test
    void createCommentWhenUserHaveNotAnyBookingsShouldThrowValidationException() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(bookingRepository.findAllByBookerAndItemAndStatus(any(User.class), any(Item.class), any(Status.class), any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());

        BookingException userBookingsNotFoundException = assertThrows(BookingException.class,
                () -> itemService.saveComment(user.getId(), item.getId(), CommentMapper.commentToDto(comment)));

        assertEquals(userBookingsNotFoundException.getMessage(), "У пользователя id - 1 нет завершенных бронирований");

    }
}
