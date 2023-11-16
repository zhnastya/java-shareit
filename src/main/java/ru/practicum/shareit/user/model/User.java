package ru.practicum.shareit.user.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @NotEmpty
    @NotNull(message = "name не может быть null")
    private String name;
    @Email
    @NotNull(message = "email не может быть null")
    private String email;
}
