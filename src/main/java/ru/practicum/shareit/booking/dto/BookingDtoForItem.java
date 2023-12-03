package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.enums.Status;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookingDtoForItem {
    private Integer id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Status status;
    private int bookerId;
}
