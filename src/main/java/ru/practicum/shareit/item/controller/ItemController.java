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
        ItemDto dto = service.createItem(userId, item);
        log.info("Товар сохранен id - " + dto.getId());
        return dto;
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") int userId,
                              @PathVariable Integer itemId,
                              @RequestBody ItemDto item) {
        log.info("Запрос на обновление товара пользователем - " + userId);
        ItemDto dto = service.updateItem(userId, itemId, item);
        log.info("Товар обновлен id - " + dto.getId());
        return dto;
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@PathVariable int itemId) {
        log.info("Запрос на получение товара - " + itemId);
        ItemDto dto = service.getByItemId(itemId);
        log.info("Товар отправлен id - " + dto.getId());
        return dto;
    }

    @GetMapping
    public List<ItemDto> getUserItems(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        log.info("Запрос на получение товаров пользователя - " + userId);
        List<ItemDto> dtoList = service.getAllByUser(userId);
        log.info("Отправлены товары пользователя - " + userId);
        return dtoList;
    }

    @GetMapping("/search")
    public List<ItemDto> getSearchItem(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                       @RequestParam(value = "text") String text) {
        log.info("Получение товаров по запросу - " + text);
        List<ItemDto> dtoList = service.getByName(userId, text);
        log.info("Отправлены товары по запросу - " + text);
        return dtoList;
    }
}
