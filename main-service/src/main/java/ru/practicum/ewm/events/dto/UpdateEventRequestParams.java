package ru.practicum.ewm.events.dto;

import lombok.Builder;
import lombok.Getter;

@Builder(toBuilder = true)
@Getter
public class UpdateEventRequestParams {
    private Long userId;
    private Long eventId;
    private UpdateEventUserRequest updateEventUserRequest;
}
