package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemFullDto;

import java.util.List;

public interface ItemService {
    ItemFullDto createItem(int userId, ItemFullDto item);

    CommentDto saveComment(int userId, int itemId, CommentDto commentDto);

    ItemFullDto updateItem(int userId, int id, ItemFullDto item);

    ItemFullDto getByItemId(int userId, int id);

    List<ItemFullDto> getAllByUser(int userId);

    List<ItemFullDto> getByName(int userId, String name);
}
