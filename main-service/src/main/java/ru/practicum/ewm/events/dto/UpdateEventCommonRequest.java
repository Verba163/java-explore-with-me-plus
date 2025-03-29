package ru.practicum.ewm.events.dto;

import lombok.Builder;
import lombok.Getter;
import ru.practicum.ewm.events.model.Location;

import java.time.LocalDateTime;

@Builder
@Getter
public class UpdateEventCommonRequest {
    private String title;
    private String description;
    private String annotation;
    private Long category;
    private Location location;
    private Boolean requestModeration;
    private Boolean paid;
    private Integer participantLimit;
    private LocalDateTime eventDate;
}
