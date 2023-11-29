package ru.practicum.shareit.item.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.item.mapper.ItemMapper.mapperToDto;
import static ru.practicum.shareit.item.mapper.ItemMapper.mapperToModel;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final UserRepository userRepository;
    private final ItemRepository repository;

    @Override
    public ItemDto createItem(int userId, ItemDto item) {
        User user = userRepository.getById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден")); //check User
        Item item1 = mapperToModel(user, item);
        repository.saveItem(item1);
        return mapperToDto(item1);
    }

    @Override
    public ItemDto updateItem(int userId, int id, ItemDto item) {
        User user = userRepository.getById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден")); //check User
        item.setId(id);
        Item item1 = mapperToModel(user, item);
        return mapperToDto(repository.updateItem(item1));
    }

    @Override
    public ItemDto getByItemId(int id) {
        return mapperToDto(repository.getItem(id)
                .orElseThrow(() -> new NotFoundException("Товар не найден")));
    }

    @Override
    public List<ItemDto> getAllByUser(int userId) {
        userRepository.getById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        return repository.findByOwnerId(userId).stream()
                .map(ItemMapper::mapperToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getByName(int userId, String name) {
        userRepository.getById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        return repository.search(name).stream()
                .map(ItemMapper::mapperToDto)
                .collect(Collectors.toList());
    }
}