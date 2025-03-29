package ru.practicum.ewm.events.dto.parameters;

import lombok.Builder;
import lombok.Getter;
import ru.practicum.ewm.events.dto.UpdateEventUserRequest;

@Builder(toBuilder = true)
@Getter
public class UpdateEventRequestParams {
    private Long userId;
    private Long eventId;
    private UpdateEventUserRequest updateEventUserRequest;
}
