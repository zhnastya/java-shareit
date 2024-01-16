package ru.practicum.shareit.booking.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.enums.SortField;

import java.util.List;

public interface BookingService {
    BookingDto saveBooking(int booker, BookingRequestDto bookingRequestDto);

    BookingDto updateStatus(int bookingId, int owner, boolean approved);

    BookingDto getBooking(int userId, int bookingId);

    List<BookingDto> getSorted(int booker, SortField state, Pageable pageable);

    List<BookingDto> getSortedByOwner(int ownerId, SortField state, Pageable pageable);
}
