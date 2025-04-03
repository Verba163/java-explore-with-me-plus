package ru.practicum.ewm.comments.dto.parameters;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.comments.model.CommentStatus;

import java.util.List;


@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GetCommentsParameters {
    Long userId;
    List<Long> eventIds;
    CommentStatus status;
    Integer from;
    Integer size;
}
