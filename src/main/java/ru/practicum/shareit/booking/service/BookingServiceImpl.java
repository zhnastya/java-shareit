package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.exeptions.BookingException;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.mapper.BookingMapper.bookingToDto;
import static ru.practicum.shareit.booking.mapper.BookingMapper.dtoToBooking;


@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository repository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    private boolean checkTimeZone(List<Booking> bookings, LocalDateTime startTime,
                                  LocalDateTime endTime) {
        return bookings.stream()
                .anyMatch(x -> x.getStatus().equals(Status.APPROVED)
                        && x.getEnd().isAfter(startTime)
                        && x.getStart().isBefore(endTime));

    }

    @Transactional
    @Override
    public BookingDto saveBooking(int bookerId, BookingRequestDto bookingRequestDto) {
        User booker = userRepository.findById(bookerId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id - " + bookerId + " не найден"));
        Item item = itemRepository.findById(bookingRequestDto.getItemId())
                .orElseThrow(() ->
                        new NotFoundException("Вещь с id - " + bookingRequestDto.getItemId() + " не найдена"));
        if (!item.getAvailable()) {
            throw new BookingException("Вещь с id - " + item.getId() + " нельзя забронировать");
        }
        if (item.getOwner().equals(booker)) {
            throw new NotFoundException("Владелец не может забронировать вещь");
        }
        if (!item.getBookings().isEmpty() && checkTimeZone(item.getBookings(), bookingRequestDto.getStart(), bookingRequestDto.getEnd())) {
            throw new BookingException("Указанное время уже забронировано");
        }

        Booking booking = dtoToBooking(bookingRequestDto);
        booking.saveBookerItem(booker, item);
        booking.setStatus(Status.WAITING);
        return bookingToDto(repository.save(booking));
    }

    @Transactional
    @Override
    public BookingDto updateStatus(int bookingId, int ownerId, boolean approved) {
            Booking booking = repository.findById(bookingId).orElseThrow(()->new NotFoundException("Бронирование не найдено, id - " + bookingId));
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id - " + ownerId + " не найден"));
        if (repository.findCustomByOwnerAndBookingId(owner, bookingId).isEmpty()) {
                throw new NotFoundException("Пользователь - " + owner + " не является владельцем вещи");
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
    }

    @Transactional(readOnly = true)
    @Override
    public BookingDto getBooking(int userId, int bookingId) {
            Booking booking = repository.findById(bookingId)
                    .orElseThrow(()->new NotFoundException("Бронирование не найдено, id - " + bookingId));
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id - " + userId + " не найден"));
            if (repository.findCustomAnyUserAndBookingId(owner, bookingId).isEmpty()) {
                throw new NotFoundException("Пользователь - " + userId + " не имеет доступ к бронированию");
            }
            return bookingToDto(booking);
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingDto> getSorted(int bookerId, Status state) {
        User booker = userRepository.findById(bookerId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id - " + bookerId + " не найден"));
        Sort sort = Sort.by(Sort.Direction.DESC, "timeOfCreated");
        List<Booking> bookings=new ArrayList<>();
        switch (state.name()) {
            case "ALL":
                bookings = repository.findAllByBooker(booker, sort);
                break;
            case "CURRENT":
                bookings = repository.findCustomByCurrent(booker, LocalDateTime.now());
                break;
            case "PAST":
                bookings = repository.findCustomByPast(booker, LocalDateTime.now(), sort);
                break;
            case "FUTURE":
                bookings = repository.findCustomByFuture(booker, LocalDateTime.now(), sort);
                break;
            case "WAITING":
                bookings = repository.findAllByBookerAndStatus(booker, Status.WAITING, sort);
                break;
            case "REJECTED":
                bookings = repository.findAllByBookerAndStatus(booker, Status.REJECTED, sort);
                break;
        }
        return bookings.stream()
                .map(BookingMapper::bookingToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingDto> getSortedByOwner(int ownerId, Status state) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id - " + ownerId + " не найден"));
        Sort sort = Sort.by(Sort.Direction.DESC, "timeOfCreated");
        List<Booking> bookings=new ArrayList<>();
        switch (state.name()) {
            case "ALL":
                bookings = repository.findCustomAllOwner(owner, sort);
                break;
            case "CURRENT":
                bookings = repository.findCustomByCurrentOwner(owner, LocalDateTime.now(), sort);
                break;
            case "PAST":
                bookings = repository.findCustomByPastOwner(owner, LocalDateTime.now(), sort);
                break;
            case "FUTURE":
                bookings = repository.findCustomByFutureOwner(owner, LocalDateTime.now(), sort);
                break;
            case "WAITING":
                bookings = repository.findCustomByStatusOwner(owner, Status.WAITING, sort);
                break;
            case "REJECTED":
                bookings = repository.findCustomByStatusOwner(owner, Status.REJECTED, sort);
                break;
        }
        return bookings.stream()
                .map(BookingMapper::bookingToDto)
                .collect(Collectors.toList());
    }
}
