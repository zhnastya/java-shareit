package ru.practicum.shareit.item.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoForBooking;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;

@Component
public class ItemMapper {

    public static ItemDto mapperToDto(int userId, Item item) {
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

        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .lastBooking(BookingMapper.mapperForItem(last))
                .nextBooking(BookingMapper.mapperForItem(next))
                .comments(item.getComments() == null
                        ? new ArrayList<>()
                        : item.getComments().stream()
                        .map(CommentMapper::commentToDto)
                        .collect(Collectors.toList()))
                .build();
    }

    public static Item mapperToModel(ItemDto item) {
        return Item.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }

    public static ItemDtoForBooking mapperForBooking(Item item) {
        return ItemDtoForBooking.builder()
                .id(item.getId())
                .name(item.getName())
                .available(item.getAvailable())
                .description(item.getDescription())
                .build();
    }
}
