package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentDto {
    private Integer id;
    @NotBlank
    private String text;
    private Integer authorId;
    private String authorName;
    private Integer itemId;
    private LocalDateTime created;
}
