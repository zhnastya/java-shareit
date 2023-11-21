package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class ItemRepoImpl implements ItemRepo {
    private final Map<Integer, Item> items = new HashMap<>();
    private int id = 1;

    @Override
    public void saveItem(Item item) {
        int newId = id++;
        item.setId(newId);
        items.put(newId, item);
    }

    @Override
    public Item updateItem(Item item) {
        Item item1 = items.get(item.getId());
        if (item1 == null || item1.getOwner().getId() != item.getOwner().getId()) {
            throw new NotFoundException("Товар не найден");
        }
        if (item.getAvailable() != null) {
            item1.setAvailable(item.getAvailable());
        }
        if (item.getName() != null) {
            item1.setName(item.getName());
        }
        if (item.getDescription() != null) {
            item1.setDescription(item.getDescription());
        }
        return item1;
    }

    @Override
    public Optional<Item> getItem(int id) {
        return Optional.of(items.get(id));
    }


    @Override
    public List<Item> findByOwnerId(int userId) {
        return items.values().stream()
                .filter(x -> x.getOwner().getId() == userId)
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> search(String text) {
        String lowerName = text.toLowerCase();
        if (text.isEmpty()) return new ArrayList<>();
        return items.values().stream()
                .filter(Item::getAvailable)
                .filter(x -> x.getName().toLowerCase().contains(lowerName)
                        || x.getDescription().toLowerCase().contains(lowerName))
                .collect(Collectors.toList());
    }
}
