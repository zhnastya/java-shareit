package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingInDto;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService service;

    @PostMapping
    public BookingOutDto saveBooking(@RequestHeader("X-Sharer-User-Id") int bookerId,
                                     @Valid @RequestBody BookingInDto dto) {
        log.info("Запрос на создание бронирования пользователем - " + bookerId);
        BookingOutDto bookingOutDto = service.saveBooking(bookerId, dto);
        log.info("Бронирование сохранено, id - " + bookingOutDto.getId());
        return bookingOutDto;
    }

    @PatchMapping("/{bookingId}")
    public BookingOutDto updateStatus(@RequestHeader("X-Sharer-User-Id") int ownerId,
                                      @PathVariable int bookingId,
                                      @RequestParam boolean approved) {
        log.info("Запрос на обновление бронирования " + bookingId + " пользователем - " + ownerId);
        BookingOutDto bookingOutDto = service.updateStatus(bookingId, ownerId, approved);
        log.info("Статус бронирования обновлен - " + bookingId + " пользователем - " + ownerId);
        return bookingOutDto;
    }

    @GetMapping("/{bookingId}")
    public BookingOutDto getBooking(@RequestHeader("X-Sharer-User-Id") int userId,
                                    @PathVariable int bookingId) {
        log.info("Запрос на просмотр бронирования - " + bookingId + " пользователем - " + userId);
        BookingOutDto bookingOutDto = service.getBooking(userId, bookingId);
        log.info("Бронирование - " + bookingId + " отправлено - ");
        return bookingOutDto;
    }

    @GetMapping
    public List<BookingOutDto> getSorted(@RequestHeader("X-Sharer-User-Id") int userId,
                                         @RequestParam(name = "state", required = false,
                                                 defaultValue = "ALL") String state) {
        log.info("Запрос на просмотр бронирований со статусом - " + state);
        List<BookingOutDto> list = service.getSorted(userId, state);
        log.info("Отправлен список бронирований со статусом - " + state);
        return list;
    }

    @GetMapping("/owner")
    public List<BookingOutDto> getByOwner(@RequestHeader("X-Sharer-User-Id") int userId,
                                          @RequestParam(name = "state", required = false,
                                                  defaultValue = "ALL") String state) {
        log.info("Запрос на просмотр бронирований владельцем со статусом - " + state);
        List<BookingOutDto> list = service.getSortedByOwner(userId, state);
        log.info("Отправлен список бронирований владельца - " + userId + " со статусом - " + state);
        return list;
    }
}
