package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto create(Integer userId, ItemRequestDto itemRequestDto);

    List<ItemRequestDto> getAllByUser(Integer userId);

    List<ItemRequestDto> getAll(int from, int size, Integer userId);

    ItemRequestDto getById(Integer requestId, Integer userId);
}
