package ru.practicum.shareit.item.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ru.practicum.shareit.item.mapper.ItemMapper.mapperToDto;
import static ru.practicum.shareit.item.mapper.ItemMapper.mapperToModel;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final UserRepository userRepository;
    private final ItemRepository repository;
    private Integer id = 1;

    private Map<Integer, Item> checkContainsItem(int userId) {
        return repository.getUserItem(userId).orElseThrow(() -> new NotFoundException("Товар не найден"));
    }

    @Override
    public ItemDto createItem(int userId, ItemDto item) {
        userRepository.getById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден")); //check User
        Integer newId = id++;
        Item item1 = mapperToModel(item);
        repository.saveItem(userId, newId, item1);
        return mapperToDto(newId, item1);
    }

    @Override
    public ItemDto updateItem(int userId, int id, ItemDto item) {
        Map<Integer, Item> items = checkContainsItem(userId);
        Item item1 = items.get(id);
        if (item.getAvailable() != null) item1.setAvailable(item.getAvailable());
        if (item.getName() != null) item1.setName(item.getName());
        if (item.getDescription() != null) item1.setDescription(item.getDescription());
        return mapperToDto(id, item1);
    }

    @Override
    public ItemDto getByItemId(int id) {
        return mapperToDto(id, repository.getItem(id).orElseThrow(() -> new NotFoundException("Товар не найден")));
    }

    @Override
    public List<ItemDto> getAllByUser(int userId) {
        userRepository.getById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Map<Integer, Item> items = checkContainsItem(userId);
        if (items == null) return new ArrayList<>();
        return items.entrySet().stream()
                .map(itemEntry -> mapperToDto(itemEntry.getKey(), itemEntry.getValue()))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getByName(int userId, String name) {
        userRepository.getById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        return repository.getByName(name).stream()
                .map(x -> mapperToDto(x.getKey(), x.getValue()))
                .collect(Collectors.toList());
    }
}