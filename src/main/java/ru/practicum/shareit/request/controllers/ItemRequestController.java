package ru.practicum.shareit.request.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    private static final String HEADER_USER_ID = "X-Sharer-User-Id";

    @PostMapping
    public ItemRequestDto create(
            @RequestHeader(HEADER_USER_ID) Integer userId,
            @Valid @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Save ItemRequest, userId - " + userId);
        ItemRequestDto dto = itemRequestService.create(userId, itemRequestDto);
        log.info("ItemRequest has been saved, id - " + dto.getId());
        return dto;
    }

    @GetMapping
    public List<ItemRequestDto> getAllByUser(
            @RequestHeader(HEADER_USER_ID) Integer userId) {
        log.info("Get all ItemRequests by userId - " + userId);
        List<ItemRequestDto> dtos = itemRequestService.getAllByUser(userId);
        log.info("ItemRequests has been sent");
        return dtos;
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAll(
            @RequestParam(defaultValue = "0", required = false) int from,
            @RequestParam(defaultValue = "10", required = false) int size,
            @RequestHeader(HEADER_USER_ID) Integer userId) {
        log.info("Get all ItemRequests");
        List<ItemRequestDto> dtos = itemRequestService.getAll(from, size, userId);
        log.info("ItemRequests has been sent");
        return dtos;
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getById(
            @PathVariable Integer requestId,
            @RequestHeader(HEADER_USER_ID) Integer userId) {
        log.info("Get ItemRequest by id - " + requestId);
        ItemRequestDto dto = itemRequestService.getById(requestId, userId);
        log.info("ItemRequest with id - " + requestId + " has been sent");
        return dto;
    }
}
