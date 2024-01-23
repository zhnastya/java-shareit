package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.enums.SortField;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RestController
@Validated
@Slf4j
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingDto saveBooking(@RequestHeader("X-Sharer-User-Id") int bookerId,
                                  @RequestBody BookingRequestDto dto) {
        log.info("Запрос на создание бронирования пользователем - " + bookerId);
        BookingDto bookingDto = service.saveBooking(bookerId, dto);
        log.info("Бронирование сохранено, id - " + bookingDto.getId());
        return bookingDto;
    }

    @PatchMapping("/{bookingId}")
    public BookingDto updateStatus(@RequestHeader("X-Sharer-User-Id") int ownerId,
                                   @PathVariable int bookingId,
                                   @RequestParam boolean approved) {
        log.info("Запрос на обновление бронирования " + bookingId + " пользователем - " + ownerId);
        BookingDto bookingDto = service.updateStatus(bookingId, ownerId, approved);
        log.info("Статус бронирования обновлен - " + bookingId + " пользователем - " + ownerId);
        return bookingDto;
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@RequestHeader("X-Sharer-User-Id") int userId,
                                 @PathVariable int bookingId) {
        log.info("Запрос на просмотр бронирования - " + bookingId + " пользователем - " + userId);
        BookingDto bookingDto = service.getBooking(userId, bookingId);
        log.info("Бронирование - " + bookingId + " отправлено - ");
        return bookingDto;
    }

    @GetMapping
    public List<BookingDto> getSorted(@RequestHeader("X-Sharer-User-Id") int userId,
                                      @RequestParam(name = "state", required = false, defaultValue = "ALL") String field,
                                      @RequestParam(defaultValue = "0", required = false) int from,
                                      @RequestParam(defaultValue = "10", required = false) int size) {
        log.info("Запрос на просмотр бронирований со статусом - " + field);
        PageRequest request = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "start"));
        List<BookingDto> list = service.getSorted(userId, SortField.valueOf(field), request);
        log.info("Отправлен список бронирований со статусом - " + field);
        return list;
    }

    @GetMapping("/owner")
    public List<BookingDto> getByOwner(@RequestHeader("X-Sharer-User-Id") int userId,
                                       @RequestParam(name = "state", required = false, defaultValue = "ALL") String field,
                                       @RequestParam(defaultValue = "0", required = false) int from,
                                       @RequestParam(defaultValue = "10", required = false) int size) {
        log.info("Запрос на просмотр бронирований владельцем со статусом - " + field);
        PageRequest request = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "start"));
        List<BookingDto> list = service.getSortedByOwner(userId, SortField.valueOf(field), request);
        log.info("Отправлен список бронирований владельца - " + userId + " со статусом - " + field);
        return list;
    }
}
