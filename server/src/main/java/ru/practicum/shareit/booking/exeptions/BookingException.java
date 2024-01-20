package ru.practicum.shareit.booking.exeptions;

public class BookingException extends RuntimeException {
    public BookingException(String message) {
        super(message);
    }
}
