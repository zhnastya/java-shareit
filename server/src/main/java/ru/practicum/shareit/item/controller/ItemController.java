package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemFullDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService service;

    @PostMapping
    public ResponseEntity<ItemFullDto> saveItem(@RequestHeader("X-Sharer-User-Id") int userId,
                                                @RequestBody ItemFullDto item) {
        log.info("Запрос на сохранение товара пользователем - " + userId);
        ItemFullDto dto = service.createItem(userId, item);
        log.info("Товар сохранен id - " + dto.getId());
        return ResponseEntity.ok(dto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemFullDto> updateItem(@RequestHeader("X-Sharer-User-Id") int userId,
                                                  @PathVariable Integer itemId,
                                                  @RequestBody ItemFullDto item) {
        log.info("Запрос на обновление товара пользователем - " + userId);
        ItemFullDto dto = service.updateItem(userId, itemId, item);
        log.info("Товар обновлен id - " + dto.getId());
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemFullDto> getItem(@RequestHeader("X-Sharer-User-Id") int userId, @PathVariable int itemId) {
        log.info("Запрос на получение товара - " + itemId);
        ItemFullDto dto = service.getByItemId(userId, itemId);
        log.info("Товар отправлен id - " + dto.getId());
        return ResponseEntity.ok(dto);
    }

    @GetMapping
    public ResponseEntity<List<ItemFullDto>> getUserItems(@RequestHeader("X-Sharer-User-Id") int userId) {
        log.info("Запрос на получение товаров пользователя - " + userId);
        List<ItemFullDto> dtoList = service.getAllByUser(userId);
        log.info("Отправлены товары пользователя - " + userId);
        return ResponseEntity.ok(dtoList);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemFullDto>> getSearchItem(@RequestHeader("X-Sharer-User-Id") int userId,
                                                           @RequestParam(value = "text") String text) {
        log.info("Получение товаров по запросу - " + text);
        List<ItemFullDto> dtoList = service.getByName(userId, text);
        log.info("Отправлены товары по запросу - " + text);
        return ResponseEntity.ok(dtoList);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<CommentDto> saveComment(@RequestHeader("X-Sharer-User-Id") int userId,
                                                  @PathVariable int itemId,
                                                  @RequestBody CommentDto commentDto) {
        log.info("Сохранение комментария от пользователя - " + userId);
        CommentDto commentDto1 = service.saveComment(userId, itemId, commentDto);
        log.info("Сохранен комментарий от пользователя - " + userId);
        return ResponseEntity.ok(commentDto1);
    }
}
