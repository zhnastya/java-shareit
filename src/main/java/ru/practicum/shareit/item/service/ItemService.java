package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto createItem(int userId, ItemDto item);

    ItemDto updateItem(int userId, int id, ItemDto item);

    ItemDto getByItemId(int id);

    List<ItemDto> getAllByUser(int userId);

    List<ItemDto> getByName(int userId, String name);

}
