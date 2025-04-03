package ru.practicum.ewm.comments.dto.parameters;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.comments.dto.UpdateCommentDto;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateCommentParameters {
    Long userId;
    Long commentId;
    UpdateCommentDto updateCommentDto;
}
