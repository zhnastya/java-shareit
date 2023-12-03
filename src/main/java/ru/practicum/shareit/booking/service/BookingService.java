package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingInDto;
import ru.practicum.shareit.booking.dto.BookingOutDto;

import java.util.List;

public interface BookingService {
    BookingOutDto saveBooking(int booker, BookingInDto bookingInDto);

    BookingOutDto updateStatus(int bookingId, int owner, boolean approved);

    BookingOutDto getBooking(int userId, int bookingId);

    List<BookingOutDto> getSorted(int booker, String state);

    List<BookingOutDto> getSortedByOwner(int ownerId, String state);
}
