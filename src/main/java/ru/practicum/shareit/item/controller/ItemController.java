package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService service;

    @PostMapping
    public ItemDto saveItem(@NotNull @RequestHeader("X-Sharer-User-Id") Integer userId,
                            @Valid @RequestBody Item item) {
        return service.createItem(userId, item);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") Integer userId,
                              @PathVariable Integer itemId,
                              @RequestBody Item item) {
        return service.updateItem(userId, itemId, item);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@PathVariable Integer itemId) {
        return service.getByItemId(itemId);
    }

    @GetMapping
    public List<ItemDto> getUserItems(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        return service.getAllByUser(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> getSearchItem(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                       @RequestParam(value = "text") String text) {
        return service.getByName(userId, text);
    }
}
