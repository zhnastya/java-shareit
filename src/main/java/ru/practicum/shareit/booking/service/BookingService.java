package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.enums.SortField;

import java.util.List;

public interface BookingService {
    BookingDto saveBooking(int booker, BookingRequestDto bookingRequestDto);

    BookingDto updateStatus(int bookingId, int owner, boolean approved);

    BookingDto getBooking(int userId, int bookingId);

    List<BookingDto> getSorted(int booker, SortField state);

    List<BookingDto> getSortedByOwner(int ownerId, SortField state);
}
