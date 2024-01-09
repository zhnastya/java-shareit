package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.validators.BookingValidator;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import java.time.LocalDateTime;

@BookingValidator.List({
        @BookingValidator(
                field = "start",
                fieldMatch = "end",
                message = "Дата старта должна быть раньше окончания"
        )
})
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookingRequestDto {
    private Integer id;
    @FutureOrPresent(message = "дата старта должна быть в будущем")
    private LocalDateTime start;
    @Future(message = "дата окончания не может быть в прошлом")
    private LocalDateTime end;
    private int itemId;
}
