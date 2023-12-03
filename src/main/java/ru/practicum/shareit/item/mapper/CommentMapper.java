package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;

public class CommentMapper {

    public static Comment dtoToComment(CommentDto commentDto) {
        return Comment.builder()
                .text(commentDto.getText())
                .build();
    }

    public static CommentDto commentToDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorId(comment.getAuthor().getId())
                .authorName(comment.getAuthor().getName())
                .itemId(comment.getItem().getId())
                .created(comment.getTimeOfCreated())
                .build();
    }
}
