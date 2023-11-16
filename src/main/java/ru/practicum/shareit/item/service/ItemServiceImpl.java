package ru.practicum.shareit.item.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.service.UserService;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final UserService service;
    private final Map<Integer, Map<Integer, Item>> userItems = new HashMap<>();
    private Integer id = 1;

    private ItemDto mapper(Integer id, Item item) {
        return ItemDto.builder()
                .id(id)
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }

    private Map<Integer, Item> checkContainsItem(Integer userId, Integer itemId) {
        service.getById(userId); //check User
        Map<Integer, Item> items = userItems.get(userId);
        if (items == null || items.get(itemId) == null) throw new NotFoundException("Товар не найден");
        return items;
    }

    @Override
    public ItemDto createItem(Integer userId, Item item) {
        service.getById(userId); //check User
        Integer newId = id++;
        Map<Integer, Item> items = userItems.get(userId) == null ? new HashMap<>() : userItems.get(userId);
        items.put(newId, item);
        userItems.put(userId, items);
        return mapper(newId, item);
    }

    @Override
    public ItemDto updateItem(Integer userId, Integer id, Item item) {
        Map<Integer, Item> items = checkContainsItem(userId, id);
        Item item1 = items.get(id);
        if (item.getAvailable() != null) item1.setAvailable(item.getAvailable());
        if (item.getName() != null) item1.setName(item.getName());
        if (item.getDescription() != null) item1.setDescription(item.getDescription());
        return mapper(id, item1);
    }

    @Override
    public ItemDto getByItemId(Integer id) {
        Optional<Item> item = userItems.values().stream()
                .filter(map -> map.containsKey(id))
                .findFirst()
                .map(map -> map.get(id));
        return mapper(id, item.orElseThrow(() -> new NotFoundException("Товар не найден")));
    }

    @Override
    public List<ItemDto> getAllByUser(Integer userId) {
        service.getById(userId); //check User
        Map<Integer, Item> items = userItems.get(userId);
        if (items == null) return new ArrayList<>();
        return items.entrySet().stream()
                .map(itemEntry -> mapper(itemEntry.getKey(), itemEntry.getValue()))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getByName(Integer userId, String name) {
        service.getById(userId); //check User
        if (name.isEmpty()) return new ArrayList<>();
        List<ItemDto> itemDtos = new ArrayList<>();
        for (Map<Integer, Item> value : userItems.values()) {
            List<ItemDto> items = value.entrySet().stream()
                    .map(itemEntry -> mapper(itemEntry.getKey(), itemEntry.getValue()))
                    .filter(item -> item.getName().toLowerCase().contains(name.toLowerCase())
                            || item.getDescription().toLowerCase().contains(name.toLowerCase()))
                    .filter(ItemDto::isAvailable)
                    .collect(Collectors.toList());
            itemDtos.addAll(items);
        }
        return itemDtos;
    }
}
