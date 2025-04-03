package ru.practicum.ewm.comments.service;

import ru.practicum.ewm.comments.dto.CommentDto;
import ru.practicum.ewm.comments.dto.NewCommentDto;
import ru.practicum.ewm.comments.dto.UpdateCommentAdminDto;
import ru.practicum.ewm.comments.dto.parameters.GetCommentsForAdminParameters;
import ru.practicum.ewm.comments.dto.parameters.GetCommentsParameters;
import ru.practicum.ewm.comments.dto.parameters.UpdateCommentParameters;

import java.util.List;

public class CommentServiceImpl implements CommentService {
    @Override
    public CommentDto createComment(Long userId, NewCommentDto newCommentDto) {
        return null;
    }

    @Override
    public List<CommentDto> getComments(GetCommentsParameters parameters) {
        return List.of();
    }

    @Override
    public CommentDto getComment(Long commentId, Long userId) {
        return null;
    }

    @Override
    public CommentDto updateComment(UpdateCommentParameters parameters) {
        return null;
    }

    @Override
    public void deleteComment(Long commentId, Long userId) {

    }

    @Override
    public List<CommentDto> getCommentsForAdmin(GetCommentsForAdminParameters parameters) {
        return List.of();
    }

    @Override
    public CommentDto updateCommentByAdmin(long commentId, UpdateCommentAdminDto updateCommentAdminDto) {
        return null;
    }
}
