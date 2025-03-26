package ru.practicum.ewm.events.service;

import ru.practicum.ewm.events.dto.EventFullDto;
import ru.practicum.ewm.events.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.events.dto.EventShortDto;
import ru.practicum.ewm.events.dto.EventsForUserRequestParams;
import ru.practicum.ewm.events.dto.NewEventDto;
import ru.practicum.ewm.events.dto.SearchEventsRequestParams;
import ru.practicum.ewm.events.dto.SearchPublicEventsRequestParams;
import ru.practicum.ewm.events.dto.UpdateEventAdminRequest;
import ru.practicum.ewm.events.dto.UpdateEventRequestParams;
import ru.practicum.ewm.events.dto.UpdateRequestsStatusRequestParams;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;

import java.util.List;

public interface EventsService {
    List<EventShortDto> getEventsCreatedByUser(EventsForUserRequestParams eventsForUserRequestParams);

    EventFullDto createEvent(Long userId, NewEventDto newEventDto);

    EventFullDto getEventById(Long userId, Long eventId);

    EventFullDto updateEvent(UpdateEventRequestParams updateEventRequestParams);

    List<ParticipationRequestDto> getRequestsForEvent(Long userId, Long eventId);

    EventRequestStatusUpdateResult updateRequestsForEvent(UpdateRequestsStatusRequestParams updateParams);

    List<EventFullDto> searchEvents(SearchEventsRequestParams searchEventsRequestParams);

    EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest updateEventAdminRequest);

    List<EventFullDto> searchPublicEvents(SearchPublicEventsRequestParams searchPublicEventsRequestParams);

    EventFullDto getPublicEventById(Long eventId);
}
