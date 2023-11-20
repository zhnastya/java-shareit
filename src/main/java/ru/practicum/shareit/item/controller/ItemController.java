package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService service;

    @PostMapping
    public ItemDto saveItem(@RequestHeader("X-Sharer-User-Id") int userId,
                            @Valid @RequestBody ItemDto item) {
        log.info("Запрос на сохранение товара пользователем - " + userId);
        return service.createItem(userId, item);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") int userId,
                              @PathVariable Integer itemId,
                              @RequestBody ItemDto item) {
        log.info("Запрос на обновление товара пользователем - " + userId);
        return service.updateItem(userId, itemId, item);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@PathVariable int itemId) {
        log.info("Запрос на получение товара - " + itemId);
        return service.getByItemId(itemId);
    }

    @GetMapping
    public List<ItemDto> getUserItems(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        log.info("Запрос на получение товаров пользователя - " + userId);
        return service.getAllByUser(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> getSearchItem(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                       @RequestParam(value = "text") String text) {
        log.info("Запрос на получение товаров по имени - " + text);
        return service.getByName(userId, text);
    }
}
