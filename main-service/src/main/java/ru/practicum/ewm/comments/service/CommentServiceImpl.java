package ru.practicum.ewm.comments.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.comments.dto.CommentDto;
import ru.practicum.ewm.comments.dto.NewCommentDto;
import ru.practicum.ewm.comments.dto.UpdateCommentAdminDto;
import ru.practicum.ewm.comments.dto.parameters.GetCommentsForAdminParameters;
import ru.practicum.ewm.comments.dto.parameters.GetCommentsParameters;
import ru.practicum.ewm.comments.dto.parameters.UpdateCommentParameters;
import ru.practicum.ewm.comments.mapper.CommentMapper;
import ru.practicum.ewm.comments.model.AdminAction;
import ru.practicum.ewm.comments.model.Comment;
import ru.practicum.ewm.comments.model.CommentStatus;
import ru.practicum.ewm.comments.storage.CommentRepository;
import ru.practicum.ewm.error.exception.ConflictException;
import ru.practicum.ewm.error.exception.NotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;

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
        CommentStatus status = parameters.getStatus();
        Integer size = parameters.getSize();
        Integer from = parameters.getFrom();
        return commentRepository.findPageableCommentsForAdmin(status, from, size).stream()
                .map(CommentMapper::toCommentDto)
                .toList();
    }

    @Override
    public CommentDto updateCommentByAdmin(long commentId, UpdateCommentAdminDto updateCommentAdminDto) {
        Comment comment = getCommentWithCheck(commentId);
        AdminAction action = updateCommentAdminDto.getAction();

        if (comment.getStatus() != CommentStatus.PENDING) {
            throw new ConflictException("Comment must has status PENDING.");
        }

        comment.setStatus(action == AdminAction.APPROVE ? CommentStatus.APPROVE : CommentStatus.REJECT);
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    private Comment getCommentWithCheck(Long id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Comment with id %d not found.", id)));
    }
}
