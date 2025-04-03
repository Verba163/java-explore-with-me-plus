package ru.practicum.ewm.comments.mapper;

import ru.practicum.ewm.comments.dto.CommentDto;
import ru.practicum.ewm.comments.dto.NewCommentDto;
import ru.practicum.ewm.comments.model.Comment;

public class CommentMapper {
    public static Comment fromNewCommentDto(NewCommentDto newCommentDto) {
        return Comment.builder()
                .build();
    }

    public static CommentDto toCommentDto(Comment comment) {
        return CommentDto.builder()
                .build();
    }
}
