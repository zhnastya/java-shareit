package ru.practicum.shareit.booking.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDtoForItem;
import ru.practicum.shareit.booking.dto.BookingInDto;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.user.mapper.UserMapper;

@Component
public class BookingMapper {

    public static Booking dtoToBooking(BookingInDto bookingInDto) {
        return Booking.builder()
                .start(bookingInDto.getStart())
                .end(bookingInDto.getEnd())
                .build();
    }

    public static BookingOutDto bookingToDto(Booking booking) {
        if (booking == null) {
            return null;
        }
        return BookingOutDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .booker(UserMapper.mapToDto(booking.getBooker()))
                .item(ItemMapper.mapperForBooking(booking.getItem()))
                .status(booking.getStatus())
                .build();
    }

    public static BookingDtoForItem mapperForItem(Booking booking) {
        if (booking == null) {
            return null;
        }
        return BookingDtoForItem.builder()
                .id(booking.getId())
                .bookerId(booking.getBooker().getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .build();
    }
}
