package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Item {
    @NotEmpty
    @NotNull(message = "name не может быть null")
    private String name;
    @NotEmpty
    private String description;
    @NotNull(message = "available не может быть null")
    private Boolean available;
}
