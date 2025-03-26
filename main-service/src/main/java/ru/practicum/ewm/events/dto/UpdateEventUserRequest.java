package ru.practicum.ewm.events.dto;

import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.events.model.Location;
import ru.practicum.ewm.events.model.StateAction;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateEventUserRequest {
    @Size(min = 3, max = 120)
    String title;

    @Size(min = 20, max = 7000)
    String description;

    @Size(min = 20, max = 2000)
    String annotation;

    Long category;

    Location location;

    Boolean requestModeration;

    Boolean paid;

    Long participantLimit;

    LocalDateTime eventDate;

    StateAction stateAction;
}