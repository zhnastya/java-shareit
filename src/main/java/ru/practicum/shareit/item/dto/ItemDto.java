package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemDto {
    private Integer id;
    @NotEmpty
    @NotNull(message = "name не может быть null")
    private String name;
    @NotEmpty
    private String description;
    @NotNull(message = "available не может быть null")
    private Boolean available;
}
