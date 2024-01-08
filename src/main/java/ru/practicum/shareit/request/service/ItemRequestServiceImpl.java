package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.item.dto.ItemFullDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static ru.practicum.shareit.request.mapper.ItemRequestMapper.fromItemRequestDto;
import static ru.practicum.shareit.request.mapper.ItemRequestMapper.toItemRequestDto;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final UserRepository userRepository;
    private final ItemRepository repository;
    private final RequestRepository itemRequestRepository;

    @Transactional
    @Override
    public ItemRequestDto create(Integer userId, ItemRequestDto itemRequestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь id - " + userId + " не найден"));
        ItemRequest itemRequest = fromItemRequestDto(itemRequestDto);
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setOwner(user);

        return toItemRequestDto(new ArrayList<>(), itemRequestRepository.save(itemRequest));
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemRequestDto> getAllByUser(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь id - " + userId + " не найден"));
        List<ItemRequest> itemRequestList = itemRequestRepository
                .findAllByOwnerOrderByCreatedAsc(user);

        return itemRequestList.stream()
                .map(x -> {
                    List<ItemFullDto> items = repository.findAllByRequest(x).stream()
                            .map(ItemMapper::mapperToDto)
                            .collect(toList());
                    return ItemRequestMapper.toItemRequestDto(items, x);
                })
                .collect(toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemRequestDto> getAll(int from, int size, Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь id - " + userId + " не найден"));
        List<ItemRequest> itemRequestList =
                itemRequestRepository.findByOwnerIsNotLike(user, PageRequest.of(from, size, Sort.by(Sort.Direction.DESC, "created")));

        return itemRequestList.stream()
                .map(x -> {
                    List<ItemFullDto> items = repository.findAllByRequest(x).stream()
                            .map(ItemMapper::mapperToDto)
                            .collect(toList());
                    return ItemRequestMapper.toItemRequestDto(items, x);
                })
                .collect(toList());
    }

    @Transactional(readOnly = true)
    @Override
    public ItemRequestDto getById(Integer requestId, Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь id - " + userId + " не найден"));
        ItemRequest itemRequest = itemRequestRepository
                .findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос с id - " + requestId + " не найден"));
        List<ItemFullDto> items = repository.findAllByRequest(itemRequest).stream()
                .map(ItemMapper::mapperToDto)
                .collect(toList());
        return toItemRequestDto(items, itemRequest);
    }
}
