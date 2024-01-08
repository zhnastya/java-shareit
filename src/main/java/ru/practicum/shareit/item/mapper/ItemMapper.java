package ru.practicum.shareit.item.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.BookingDtoForItem;
import ru.practicum.shareit.item.dto.ItemFullDto;
import ru.practicum.shareit.item.model.Item;

@Component
public class ItemMapper {

    public static ItemFullDto mapperToDto(Item item) {
        Integer requestId = item.getRequest() != null ? item.getRequest().getId() : null;
        return ItemFullDto.builder()
                .id(item.getId())
                .requestId(requestId)
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .nextBooking(null)
                .lastBooking(null)
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
