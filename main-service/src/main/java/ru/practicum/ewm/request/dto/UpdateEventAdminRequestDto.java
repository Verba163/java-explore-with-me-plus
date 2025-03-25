package ru.practicum.ewm.request.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.events.model.Location;
import ru.practicum.ewm.events.model.State;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateEventAdminRequestDto {

    String title;

    String description;

    String annotation;

    Long category;

    Location location;

    boolean requestModeration;

    boolean paid;

    Long participantLimit;

    LocalDateTime eventDate;

    State state;

}