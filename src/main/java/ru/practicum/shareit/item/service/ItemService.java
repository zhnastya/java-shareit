package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    ItemDto createItem(Integer userId, Item item);

    ItemDto updateItem(Integer userId, Integer id, Item item);

    ItemDto getByItemId(Integer id);

    List<ItemDto> getAllByUser(Integer userId);

    List<ItemDto> getByName(Integer userId, String name);

}
