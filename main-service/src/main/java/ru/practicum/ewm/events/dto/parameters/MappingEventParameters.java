package ru.practicum.ewm.events.dto.parameters;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.user.dto.UserShortDto;

@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MappingEventParameters {
    Event event;
    CategoryDto categoryDto;
    UserShortDto initiator;
    Long confirmedRequests;
    Long views;
}
