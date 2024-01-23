package ru.practicum.request.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.item.dto.ItemFullDto;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ItemRequestDto {
    private Integer id;
    @NotBlank(message = "Укажите описание")
    private String description;

    private LocalDateTime created;

    private List<ItemFullDto> items;
}
