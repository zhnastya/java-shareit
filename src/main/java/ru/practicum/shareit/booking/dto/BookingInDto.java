package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookingInDto {
    @NotNull
    @FutureOrPresent(message = "дата старта должна быть в будущем")
    private LocalDateTime start;
    @NotNull
    @Future(message = "дата окончания не может быть в прошлом")
    private LocalDateTime end;
    private int itemId;
}
