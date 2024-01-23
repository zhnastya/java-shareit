package ru.practicum.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.item.dto.CommentDto;
import ru.practicum.item.dto.ItemFullDto;

import javax.validation.Valid;

@RestController
@Slf4j
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemClient client;

    @PostMapping
    public ResponseEntity<Object> saveItem(@RequestHeader("X-Sharer-User-Id") int userId,
                                           @Valid @RequestBody ItemFullDto item) {
        log.info("Запрос на сохранение товара пользователем - " + userId);
        return client.saveItem(userId, item);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader("X-Sharer-User-Id") int userId,
                                             @PathVariable Integer itemId,
                                             @RequestBody ItemFullDto item) {
        log.info("Запрос на обновление товара пользователем - " + userId);
        return client.updateItem(userId, item, itemId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@RequestHeader("X-Sharer-User-Id") int userId, @PathVariable int itemId) {
        log.info("Запрос на получение товара - " + itemId);
        return client.getItemId(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getUserItems(@RequestHeader("X-Sharer-User-Id") int userId) {
        log.info("Запрос на получение товаров пользователя - " + userId);
        return client.getUserItems(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getSearchItem(@RequestHeader("X-Sharer-User-Id") int userId,
                                                @RequestParam(value = "text") String text) {
        log.info("Получение товаров по запросу - " + text);
        return client.getSearchItems(userId, text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> saveComment(@RequestHeader("X-Sharer-User-Id") int userId,
                                              @PathVariable int itemId,
                                              @Valid @RequestBody CommentDto commentDto) {
        log.info("Сохранение комментария от пользователя - " + userId);
        return client.saveComment(itemId, commentDto, userId);
    }
}

