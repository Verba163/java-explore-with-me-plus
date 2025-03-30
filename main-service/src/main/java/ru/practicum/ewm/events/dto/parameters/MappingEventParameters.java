package ru.practicum.ewm.events.dto.parameters;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.user.dto.UserShortDto;

@Getter
@Setter
@Builder
public class MappingEventParameters {
    private Event event;
    private CategoryDto categoryDto;
    private UserShortDto initiator;
    private Long confirmedRequests;
    private Long views;
}
