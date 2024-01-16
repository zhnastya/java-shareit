package ru.practicum.shareit.booking;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.enums.SortField;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.exeptions.BookingException;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private final User user = User.builder()
            .id(1)
            .name("username")
            .email("email@email.com")
            .bookings(new ArrayList<>())
            .comments(new ArrayList<>())
            .build();

    private final User owner = User.builder()
            .id(2)
            .name("username2")
            .email("email2@email.com")
            .bookings(new ArrayList<>())
            .comments(new ArrayList<>())
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
            .owner(owner)
            .build();

    private final Booking booking = Booking.builder()
            .id(1)
            .start(LocalDateTime.now().plusDays(1L))
            .end(LocalDateTime.now().plusDays(2L))
            .status(Status.APPROVED)
            .item(item)
            .booker(user)
            .build();

    private final Booking bookingWaiting = Booking.builder()
            .id(1)
            .start(LocalDateTime.now().plusDays(1L))
            .end(LocalDateTime.now().plusDays(2L))
            .status(Status.WAITING)
            .item(item)
            .booker(user)
            .build();

    private final BookingRequestDto requestDto = BookingRequestDto.builder()
            .id(1)
            .start(LocalDateTime.now().plusDays(1L))
            .end(LocalDateTime.now().plusDays(2L))
            .build();


    @Test
    void create() {
        BookingDto expectedBookingDtoOut = BookingMapper.bookingToDto(booking);
        when(userRepository.findById(userDto.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingDto actualBookingDtoOut = bookingService.saveBooking(userDto.getId(), requestDto);

        assertEquals(expectedBookingDtoOut, actualBookingDtoOut);
    }

    @Test
    void createWhenItemIsNotAvailableShouldThrowValidationException() {
        item.setAvailable(false);
        when(userRepository.findById(userDto.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));

        BookingException bookingValidationException = assertThrows(BookingException.class,
                () -> bookingService.saveBooking(userDto.getId(), requestDto));

        assertEquals(bookingValidationException.getMessage(), "Вещь с id - 1 нельзя забронировать");
    }

    @Test
    void createWhenItemOwnerEqualsBookerShouldThrowValidationException() {
        item.setOwner(user);
        when(userRepository.findById(userDto.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));

        NotFoundException bookingNotFoundException = assertThrows(NotFoundException.class,
                () -> bookingService.saveBooking(userDto.getId(), requestDto));

        assertEquals(bookingNotFoundException.getMessage(), "Владелец не может забронировать вещь");
    }

    @Test
    void update() {
        when(userRepository.findById(userDto.getId())).thenReturn(Optional.of(owner));
        when(bookingRepository.findCustomByOwnerAndBookingId(any(User.class), anyInt())).thenReturn(Optional.of(bookingWaiting));
        when(bookingRepository.findById(anyInt())).thenReturn(Optional.of(bookingWaiting));

        BookingDto actualBookingDtoOut = bookingService.updateStatus(owner.getId(), bookingWaiting.getId(), true);

        assertEquals(Status.APPROVED, actualBookingDtoOut.getStatus());
    }

    @Test
    void updateWhenStatusNotApproved() {
        when(userRepository.findById(userDto.getId())).thenReturn(Optional.of(owner));
        when(bookingRepository.findCustomByOwnerAndBookingId(any(User.class), anyInt())).thenReturn(Optional.of(bookingWaiting));
        when(bookingRepository.findById(anyInt())).thenReturn(Optional.of(bookingWaiting));

        BookingDto actualBookingDtoOut = bookingService.updateStatus(owner.getId(), bookingWaiting.getId(), false);

        assertEquals(Status.REJECTED, actualBookingDtoOut.getStatus());
    }

    @Test
    void updateShouldStatusNotWaiting() {
        when(userRepository.findById(userDto.getId())).thenReturn(Optional.of(owner));
        when(bookingRepository.findCustomByOwnerAndBookingId(any(User.class), anyInt())).thenReturn(Optional.of(bookingWaiting));
        when(bookingRepository.findById(anyInt())).thenReturn(Optional.of(booking));

        BookingException bookingValidationException = assertThrows(BookingException.class,
                () -> bookingService.updateStatus(owner.getId(), booking.getId(), false));

        assertEquals(bookingValidationException.getMessage(), "Статус уже подтвержден");
    }

    @Test
    void updateWhenUserIsNotItemOwnerShouldThrowNotFoundException() {
        when(userRepository.findById(userDto.getId())).thenReturn(Optional.of(owner));
        when(bookingRepository.findById(anyInt())).thenReturn(Optional.of(booking));

        NotFoundException bookingNotFoundException = assertThrows(NotFoundException.class,
                () -> bookingService.updateStatus(userDto.getId(), booking.getId(), true));

        assertEquals(bookingNotFoundException.getMessage(), "Пользователь - 2 не является владельцем вещи");
    }

    @Test
    void getById() {
        BookingDto expectedBookingDtoOut = BookingMapper.bookingToDto(booking);
        when(userRepository.findById(userDto.getId())).thenReturn(Optional.of(user));
        when(bookingRepository.findById(anyInt())).thenReturn(Optional.of(booking));
        when(bookingRepository.findCustomAnyUserAndBookingId(any(User.class), anyInt())).thenReturn(Optional.of(booking));

        BookingDto actualBookingDtoOut = bookingService.getBooking(user.getId(), booking.getId());

        assertEquals(expectedBookingDtoOut, actualBookingDtoOut);
    }

    @Test
    void getByIdWhenBookingIdIsNotValidShouldThrowObjectNotFoundException() {
        when(bookingRepository.findById(anyInt())).thenReturn(Optional.empty());

        NotFoundException bookingNotFoundException = assertThrows(NotFoundException.class,
                () -> bookingService.getBooking(1, booking.getId()));

        assertEquals(bookingNotFoundException.getMessage(), "Бронирование не найдено, id - 1");
    }

    @Test
    void getByIdWhenUserIsNotItemOwnerShouldThrowObjectNotFoundException() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(bookingRepository.findById(anyInt())).thenReturn(Optional.of(booking));

        NotFoundException bookingNotFoundException = assertThrows(NotFoundException.class,
                () -> bookingService.getBooking(3, booking.getId()));

        assertEquals(bookingNotFoundException.getMessage(), "Пользователь - 3 не имеет доступ к бронированию");
    }

    @Test
    void getAllByBookerWhenBookingStateAll() {
        List<BookingDto> expectedBookingsDtoOut = List.of(BookingMapper.bookingToDto(booking));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByBooker(any(User.class), any(Pageable.class))).thenReturn(List.of(booking));

        List<BookingDto> actualBookingsDtoOut = bookingService.getSorted(user.getId(), SortField.ALL, PageRequest.of(0, 10));

        assertEquals(expectedBookingsDtoOut, actualBookingsDtoOut);
    }

    @Test
    void getAllByBooker_whenBookingStateCURRENT() {
        List<BookingDto> expectedBookingsDtoOut = List.of(BookingMapper.bookingToDto(booking));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(bookingRepository.findCustomByCurrent(any(User.class), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(List.of(booking));

        List<BookingDto> actualBookingsDtoOut = bookingService.getSorted(user.getId(), SortField.CURRENT, PageRequest.of(0, 10));

        assertEquals(expectedBookingsDtoOut, actualBookingsDtoOut);
    }

    @Test
    void getAllByBooker_whenBookingStateWAITING() {
        List<BookingDto> expectedBookingsDtoOut = List.of(BookingMapper.bookingToDto(booking));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByBookerAndStatus(any(User.class), any(Status.class), any(Pageable.class)))
                .thenReturn(List.of(booking));

        List<BookingDto> actualBookingsDtoOut = bookingService.getSorted(user.getId(), SortField.WAITING, PageRequest.of(0, 10));

        assertEquals(expectedBookingsDtoOut, actualBookingsDtoOut);
    }

    @Test
    void getAllByBooker_whenBookingStateREJECTED() {
        List<BookingDto> expectedBookingsDtoOut = List.of(BookingMapper.bookingToDto(booking));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByBookerAndStatus(any(User.class), any(Status.class), any(Pageable.class)))
                .thenReturn(List.of(booking));

        List<BookingDto> actualBookingsDtoOut = bookingService.getSorted(user.getId(), SortField.REJECTED, PageRequest.of(0, 10));

        assertEquals(expectedBookingsDtoOut, actualBookingsDtoOut);
    }

    @Test
    void getAllByBookerWhenBookingStatePAST() {
        List<BookingDto> expectedBookingsDtoOut = List.of(BookingMapper.bookingToDto(booking));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(bookingRepository.findCustomByPast(any(User.class), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(List.of(booking));

        List<BookingDto> actualBookingsDtoOut = bookingService.getSorted(user.getId(), SortField.PAST, PageRequest.of(0, 10));

        assertEquals(expectedBookingsDtoOut, actualBookingsDtoOut);
    }

    @Test
    void getAllByBookerWhenBookingStateFUTURE() {
        List<BookingDto> expectedBookingsDtoOut = List.of(BookingMapper.bookingToDto(booking));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(bookingRepository.findCustomByFuture(any(User.class), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(List.of(booking));

        List<BookingDto> actualBookingsDtoOut = bookingService.getSorted(user.getId(), SortField.FUTURE, PageRequest.of(0, 10));

        assertEquals(expectedBookingsDtoOut, actualBookingsDtoOut);
    }


    @Test
    void getAllByBookerWhenBookingStateIsNotValidShouldThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> bookingService.getSorted(user.getId(), SortField.valueOf("Error"), PageRequest.of(0, 10)));
    }

    @Test
    void getAllByOwnerWhenBookingStateAll() {
        List<BookingDto> expectedBookingsDtoOut = List.of(BookingMapper.bookingToDto(booking));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(owner));
        when(bookingRepository.findCustomAllOwner(any(User.class), any(Pageable.class)))
                .thenReturn(List.of(booking));

        List<BookingDto> actualBookingsDtoOut = bookingService.getSortedByOwner(user.getId(), SortField.ALL, PageRequest.of(0, 10));

        assertEquals(expectedBookingsDtoOut, actualBookingsDtoOut);
    }

    @Test
    void getAllByOwnerWhenBookingStateCURRENT() {
        List<BookingDto> expectedBookingsDtoOut = List.of(BookingMapper.bookingToDto(booking));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(owner));
        when(bookingRepository.findCustomByCurrentOwner(any(User.class), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(List.of(booking));

        List<BookingDto> actualBookingsDtoOut = bookingService.getSortedByOwner(user.getId(), SortField.CURRENT, PageRequest.of(0, 10));

        assertEquals(expectedBookingsDtoOut, actualBookingsDtoOut);
    }

    @Test
    void getAllByOwnerWhenBookingStatePAST() {
        List<BookingDto> expectedBookingsDtoOut = List.of(BookingMapper.bookingToDto(booking));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(owner));
        when(bookingRepository.findCustomByPastOwner(any(User.class), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(List.of(booking));

        List<BookingDto> actualBookingsDtoOut = bookingService.getSortedByOwner(user.getId(), SortField.PAST, PageRequest.of(0, 10));

        assertEquals(expectedBookingsDtoOut, actualBookingsDtoOut);
    }

    @Test
    void getAllByOwnerWhenBookingStateWAITING() {
        List<BookingDto> expectedBookingsDtoOut = List.of(BookingMapper.bookingToDto(booking));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(owner));
        when(bookingRepository.findCustomByStatusOwner(any(User.class), any(Status.class), any(Pageable.class)))
                .thenReturn(List.of(booking));

        List<BookingDto> actualBookingsDtoOut = bookingService.getSortedByOwner(user.getId(), SortField.WAITING, PageRequest.of(0, 10));

        assertEquals(expectedBookingsDtoOut, actualBookingsDtoOut);
    }

    @Test
    void getAllByOwnerWhenBookingStateREJECTED() {
        List<BookingDto> expectedBookingsDtoOut = List.of(BookingMapper.bookingToDto(booking));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(owner));
        when(bookingRepository.findCustomByStatusOwner(any(User.class), any(Status.class), any(Pageable.class)))
                .thenReturn(List.of(booking));

        List<BookingDto> actualBookingsDtoOut = bookingService.getSortedByOwner(user.getId(), SortField.REJECTED, PageRequest.of(0, 10));

        assertEquals(expectedBookingsDtoOut, actualBookingsDtoOut);
    }

    @Test
    void getAllByOwnerWhenBookingStateFUTURE() {
        List<BookingDto> expectedBookingsDtoOut = List.of(BookingMapper.bookingToDto(booking));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(owner));
        when(bookingRepository.findCustomByFutureOwner(any(User.class), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(List.of(booking));

        List<BookingDto> actualBookingsDtoOut = bookingService.getSortedByOwner(user.getId(), SortField.FUTURE, PageRequest.of(0, 10));

        assertEquals(expectedBookingsDtoOut, actualBookingsDtoOut);
    }
}
