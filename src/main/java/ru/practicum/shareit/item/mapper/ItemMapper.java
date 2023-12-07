package ru.practicum.shareit.item.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.BookingDtoForItem;
import ru.practicum.shareit.item.dto.ItemFullDto;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;

@Component
public class ItemMapper {

    public static ItemFullDto mapperToDto(int userId, Item item) {
        LocalDateTime dateTime = LocalDateTime.now();
        Booking last = null;
        Booking next = null;
        if (item.getOwner().getId().equals(userId)) {
            last = item.getBookings().stream()
                    .filter(x -> x.getEnd().isBefore(dateTime)
                            || x.getStart().isBefore(dateTime)
                            && x.getEnd().isAfter(dateTime)
                            && x.getStatus().equals(Status.APPROVED))
                    .max(Comparator.comparing(Booking::getEnd))
                    .orElse(null);
            next = item.getBookings().stream()
                    .filter(x -> x.getStart().isAfter(dateTime)
                            && x.getStatus().equals(Status.APPROVED))
                    .min(Comparator.comparing(Booking::getStart))
                    .orElse(null);
        }

        return ItemFullDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .lastBooking(bookingDtoForItem(last))
                .nextBooking(bookingDtoForItem(next))
                .comments(item.getComments() == null
                        ? new ArrayList<>()
                        : item.getComments().stream()
                        .map(CommentMapper::commentToDto)
                        .collect(Collectors.toList()))
                .build();
    }

    public static Item mapperToModel(ItemFullDto item) {
        return Item.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }

    public static BookingDtoForItem bookingDtoForItem(Booking booking) {
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
