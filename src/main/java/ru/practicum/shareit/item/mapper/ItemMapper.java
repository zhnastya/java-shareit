package ru.practicum.shareit.item.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@Component
public class ItemMapper {

    public static ItemDto mapperToDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }

    public static Item mapperToModel(User user, ItemDto item) {
        return Item.builder()
                .id(item.getId() != null ? item.getId() : 0)
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .owner(user)
                .build();
    }
}
