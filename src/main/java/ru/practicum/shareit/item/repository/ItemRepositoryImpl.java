package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class ItemRepositoryImpl implements ItemRepository {
    private final Map<Integer, Item> items = new HashMap<>();
    private final Map<Integer, Map<Integer, Item>> userItems = new HashMap<>();
    private int id = 1;

    private void updateUserMap() {
        items.forEach((k, v) -> {
            Map<Integer, Item> itemMap = userItems.containsKey(v.getOwner().getId())
                    ? userItems.get(v.getOwner().getId())
                    : new HashMap<>();
            itemMap.put(k, v);
            userItems.put(v.getOwner().getId(), itemMap);
        });
    }

    @Override
    public void saveItem(Item item) {
        int newId = id++;
        item.setId(newId);
        items.put(newId, item);
        updateUserMap();
    }

    @Override
    public Item updateItem(Item item) {
        Item item1 = items.get(item.getId());
        if (item1 == null || !item1.getOwner().getId().equals(item.getOwner().getId())) {
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
        updateUserMap();
        return item1;
    }

    @Override
    public Optional<Item> getItem(int id) {
        return Optional.of(items.get(id));
    }


    @Override
    public List<Item> findByOwnerId(int userId) {
        if (!userItems.containsKey(userId)) return new ArrayList<>();
        return new ArrayList<>(userItems.get(userId).values());
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
