package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.*;

@Repository
public class ItemRepository {
    private final Map<Integer, Map<Integer, Item>> userItems = new HashMap<>();

    public void saveItem(int userId, int id, Item item) {
        Map<Integer, Item> itemMap = userItems.get(userId) == null ? new HashMap<>() : userItems.get(userId);
        itemMap.put(id, item);
        userItems.put(userId, itemMap);
    }

    public Collection<Map<Integer, Item>> getAll() {
        return userItems.values();
    }

    public Optional<Item> getItem(int id) {
        return userItems.values().stream()
                .filter(x -> x.containsKey(id))
                .findFirst().flatMap(x -> Optional.ofNullable(x.get(id)));
    }

    public Optional<Map<Integer, Item>> getUserItem(int userId) {
        return userItems.containsKey(userId) ? Optional.of(userItems.get(userId)) : Optional.empty();
    }

    public List<Map.Entry<Integer, Item>> getByName(String name) {
        String lowerName = name.toLowerCase();
        List<Map.Entry<Integer, Item>> items = new ArrayList<>();
        if (name.isEmpty()) return items;
        userItems.values().stream()
                .filter(x -> x.values()
                        .stream()
                        .filter(Item::getAvailable)
                        .anyMatch(s -> s.getName().toLowerCase().contains(lowerName)
                                || s.getDescription().toLowerCase().contains(lowerName)))
                .map(Map::entrySet)
                .forEach(items::addAll);
        return items;
    }
}
