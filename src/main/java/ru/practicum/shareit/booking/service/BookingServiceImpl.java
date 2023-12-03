package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingInDto;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.exeptions.BookingException;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.mapper.BookingMapper.bookingToDto;
import static ru.practicum.shareit.booking.mapper.BookingMapper.dtoToBooking;


@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository repository;
    private final ItemService itemService;
    private final UserService userService;

    private boolean checkTimeZone(List<Booking> bookings, LocalDateTime startTime,
                                  LocalDateTime endTime) {
        return bookings.stream()
                .anyMatch(x -> x.getStatus().equals(Status.APPROVED)
                        && x.getEnd().isAfter(startTime)
                        && x.getStart().isBefore(endTime));

    }

    @Override
    public BookingOutDto saveBooking(int bookerId, BookingInDto bookingInDto) {
        if (bookingInDto.getStart().isAfter(bookingInDto.getEnd())
                || bookingInDto.getStart().isEqual(bookingInDto.getEnd())) {
            throw new BookingException("Дата старта должна быть раньше окончания");
        }

        User booker = userService.getByIdModel(bookerId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id - " + bookerId + " не найден"));
        Item item = itemService.getItemForBooking(bookingInDto.getItemId())
                .orElseThrow(() ->
                        new NotFoundException("Вещь с id - " + bookingInDto.getItemId() + " не найдена"));
        if (!item.getAvailable()) {
            throw new BookingException("Вещь с id - " + item.getId() + " нельзя забронировать");
        }
        if (item.getOwner().equals(booker)) {
            throw new NotFoundException("Владелец не может забронировать вещь");
        }
        if (!item.getBookings().isEmpty() && checkTimeZone(item.getBookings(), bookingInDto.getStart(), bookingInDto.getEnd())) {
            throw new BookingException("Указанное время уже забронировано");
        }

        Booking booking = dtoToBooking(bookingInDto);
        booking.saveBookerItem(booker, item);
        booking.setStatus(Status.WAITING);
        return bookingToDto(repository.save(booking));
    }

    @Transactional
    @Override
    public BookingOutDto updateStatus(int bookingId, int owner, boolean approved) {
        try {
            Booking booking = repository.getReferenceById(bookingId);
            if (booking.getItem().getOwner().getId() != owner) {
                throw new NotFoundException("Пользователь - " + owner + "не является владельцем вещи");
            }
            if (booking.getStatus().equals(Status.APPROVED)) {
                throw new BookingException("Статус уже подтвержден");
            }
            if (approved) {
                booking.setStatus(Status.APPROVED);
            } else {
                booking.setStatus(Status.REJECTED);
            }
            return bookingToDto(booking);

        } catch (EntityNotFoundException exception) {
            throw new BookingException("Бронирование не найдено, id - " + bookingId);
        }
    }

    @Override
    public BookingOutDto getBooking(int userId, int bookingId) {
        try {
            Booking booking = repository.getReferenceById(bookingId);
            if (booking.getItem().getOwner().getId() != userId
                    && booking.getBooker().getId() != userId) {
                throw new NotFoundException("Пользователь - " + userId + " не имеет доступ к бронированию");
            }
            return bookingToDto(booking);

        } catch (EntityNotFoundException exception) {
            throw new NotFoundException("Бронирование не найдено, id - " + bookingId);
        }
    }

    @Override
    public List<BookingOutDto> getSorted(int bookerId, String state) {
        User booker = userService.getByIdModel(bookerId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id - " + bookerId + " не найден"));
        List<Booking> bookings;
        switch (state) {
            case "ALL":
                bookings = repository.findAllByBookerOrderByTimeOfCreatedDesc(booker);
                break;
            case "CURRENT":
                bookings = repository.findCustomByCurrent(booker, LocalDateTime.now());
                break;
            case "PAST":
                bookings = repository.findCustomByPast(booker, LocalDateTime.now());
                break;
            case "FUTURE":
                bookings = repository.findCustomByFuture(booker, LocalDateTime.now());
                break;
            case "WAITING":
                bookings = repository.findAllByBookerAndStatusOrderByTimeOfCreatedDesc(booker, Status.WAITING);
                break;
            case "REJECTED":
                bookings = repository.findAllByBookerAndStatusOrderByTimeOfCreatedDesc(booker, Status.REJECTED);
                break;
            default:
                throw new UnsupportedOperationException("Unknown state: " + state);
        }
        return bookings.stream()
                .map(BookingMapper::bookingToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingOutDto> getSortedByOwner(int ownerId, String state) {
        User owner = userService.getByIdModel(ownerId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id - " + ownerId + " не найден"));
        List<Booking> bookings;
        switch (state) {
            case "ALL":
                bookings = repository.findCustomAllOwner(owner);
                break;
            case "CURRENT":
                bookings = repository.findCustomByCurrentOwner(owner, LocalDateTime.now());
                break;
            case "PAST":
                bookings = repository.findCustomByPastOwner(owner, LocalDateTime.now());
                break;
            case "FUTURE":
                bookings = repository.findCustomByFutureOwner(owner, LocalDateTime.now());
                break;
            case "WAITING":
                bookings = repository.findCustomByStatusOwner(owner, Status.WAITING);
                break;
            case "REJECTED":
                bookings = repository.findCustomByStatusOwner(owner, Status.REJECTED);
                break;
            default:
                throw new UnsupportedOperationException("Unknown state: " + state);
        }
        return bookings.stream()
                .map(BookingMapper::bookingToDto)
                .collect(Collectors.toList());
    }
}
