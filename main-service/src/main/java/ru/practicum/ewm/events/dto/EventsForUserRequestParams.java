package ru.practicum.ewm.events.dto;

import lombok.Builder;
import lombok.Getter;

@Builder(toBuilder = true)
@Getter
public class EventsForUserRequestParams {
    private Long userId;
    private Integer from;
    private Integer size;
}
