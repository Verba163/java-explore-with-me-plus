package ru.practicum.ewm.events.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.events.model.EventPublishState;
import ru.practicum.ewm.events.model.Location;
import ru.practicum.ewm.user.dto.UserShortDto;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventFullDto {

    Long id;

    String title;

    Long views;

    Long participantLimit;

    CategoryDto category;

    String description;

    UserShortDto initiator;

    String annotation;

    LocalDateTime createdOn;

    LocalDateTime eventDate;

    LocalDateTime publishedOn;

    boolean requestModeration;

    Long confirmedRequests;

    EventPublishState eventPublishState;

    Location location;

    boolean paid;

}
