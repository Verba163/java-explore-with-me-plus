package ru.practicum.ewm.events.service;

import org.springframework.stereotype.Service;
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

@Service
public class EventsServiceImpl implements EventsService {
    @Override
    public List<EventShortDto> getEventsCreatedByUser(EventsForUserRequestParams eventsForUserRequestParams) {
        return List.of();
    }

    @Override
    public EventFullDto createEvent(Long userId, NewEventDto newEventDto) {
        return null;
    }

    @Override
    public EventFullDto getEventById(Long userId, Long eventId) {
        return null;
    }

    @Override
    public EventFullDto updateEvent(UpdateEventRequestParams updateEventRequestParams) {
        return null;
    }

    @Override
    public List<ParticipationRequestDto> getRequestsForEvent(Long userId, Long eventId) {
        return List.of();
    }

    @Override
    public EventRequestStatusUpdateResult updateRequestsForEvent(UpdateRequestsStatusRequestParams updateParams) {
        return null;
    }

    @Override
    public List<EventFullDto> searchEvents(SearchEventsRequestParams searchEventsRequestParams) {
        return List.of();
    }

    @Override
    public EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest updateEventAdminRequest) {
        return null;
    }

    @Override
    public List<EventFullDto> searchPublicEvents(SearchPublicEventsRequestParams searchPublicEventsRequestParams) {
        return List.of();
    }

    @Override
    public EventFullDto getPublicEventById(Long eventId) {
        return null;
    }
}
