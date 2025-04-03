package ru.practicum.ewm.comments.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.comments.model.AdminAction;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
public class UpdateCommentAdminDto {
    @NotNull
    AdminAction action;
}
