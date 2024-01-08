package ru.practicum.shareit.request.mapper;

import ru.practicum.shareit.item.dto.ItemFullDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public class ItemRequestMapper {
    public static ItemRequest fromItemRequestDto(ItemRequestDto itemRequestDto) {
        return ItemRequest
                .builder()
                .id(itemRequestDto.getId())
                .description(itemRequestDto.getDescription())
                .build();
    }

    public static ItemRequestDto toItemRequestDto(List<ItemFullDto> items, ItemRequest itemRequest) {
        return ItemRequestDto
                .builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .items(items)
                .build();
    }
}