package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemService {
    ItemDto createItem(int userId, ItemDto item);

    CommentDto saveComment(int userId, int itemId, CommentDto commentDto);

    ItemDto updateItem(int userId, int id, ItemDto item);

    ItemDto getByItemId(int userId, int id);

    List<ItemDto> getAllByUser(int userId);

    List<ItemDto> getByName(int userId, String name);

    Optional<Item> getItemForBooking(int id);
}
