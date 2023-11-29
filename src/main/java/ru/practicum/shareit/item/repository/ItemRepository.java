package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {
    void saveItem(Item item);

    Optional<Item> getItem(int id);

    List<Item> findByOwnerId(int userId);

    List<Item> search(String text);

    Item updateItem(Item item);
}
