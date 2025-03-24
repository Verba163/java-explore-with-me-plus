package ru.practicum.ewm.events.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.events.model.Location;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewEventDto {

    Long id;

    String title;

    String description;

    String annotation;

    Long category;

    Location location;

    boolean requestModeration;

    boolean paid;

    Long participantLimit;

    LocalDateTime eventDate;

}