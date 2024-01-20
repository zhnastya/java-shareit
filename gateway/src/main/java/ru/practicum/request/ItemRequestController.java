package ru.practicum.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.request.dto.ItemRequestDto;

import javax.validation.Valid;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final RequestClient client;

    private static final String HEADER_USER_ID = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> create(
            @RequestHeader(HEADER_USER_ID) int userId,
            @Valid @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Save ItemRequest, userId - " + userId);
        return client.createRequest(userId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getAllByUser(
            @RequestHeader(HEADER_USER_ID) Integer userId) {
        log.info("Get all ItemRequests by userId - " + userId);
        return client.getAllByUser(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAll(
            @RequestParam(defaultValue = "0", required = false) int from,
            @RequestParam(defaultValue = "10", required = false) int size,
            @RequestHeader(HEADER_USER_ID) Integer userId) {
        log.info("Get all ItemRequests");
        return client.getAll(from, size, userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getById(
            @PathVariable Integer requestId,
            @RequestHeader(HEADER_USER_ID) Long userId) {
        log.info("Get ItemRequest by id - " + requestId);
        return client.getById(requestId, userId);
    }
}
