package ru.practicum.ewm.events.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.category.mapper.CategoryMapper;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.events.dto.EventFullDto;
import ru.practicum.ewm.events.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.events.dto.EventShortDto;
import ru.practicum.ewm.events.dto.EventsForUserRequestParams;
import ru.practicum.ewm.events.dto.NewEventDto;
import ru.practicum.ewm.events.dto.SearchEventsRequestParams;
import ru.practicum.ewm.events.dto.SearchPublicEventsRequestParams;
import ru.practicum.ewm.events.dto.UpdateEventAdminRequest;
import ru.practicum.ewm.events.dto.UpdateEventRequestParams;
import ru.practicum.ewm.events.dto.UpdateEventUserRequest;
import ru.practicum.ewm.events.dto.UpdateRequestsStatusRequestParams;
import ru.practicum.ewm.events.exceptions.EventCreationException;
import ru.practicum.ewm.events.mapper.EventDtoParams;
import ru.practicum.ewm.events.mapper.EventMapper;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.events.model.EventPublishState;
import ru.practicum.ewm.events.model.Location;
import ru.practicum.ewm.events.storage.EventsRepository;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.user.mapper.UserMapper;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;
import ru.practicum.ewm.util.Util;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventsServiceImpl implements EventsService {
    private final EventsRepository eventsRepository;
    private final UserRepository userRepository;

    @Override
    public List<EventShortDto> getEventsCreatedByUser(EventsForUserRequestParams eventsForUserRequestParams) {
        Long userId = eventsForUserRequestParams.getUserId();
        Integer from = eventsForUserRequestParams.getFrom();
        Integer size = eventsForUserRequestParams.getSize();

        checkUserExisting(userId);

        Pageable page = createPageableObject(from, size);
        Page<Event> userEvents = eventsRepository.findAllByInitiatorIdIs(userId, page);

        return userEvents.stream()
                .map(this::createEventShortDto)
                .toList();
    }

    @Override
    public EventFullDto createEvent(Long userId, NewEventDto newEventDto) {
        LocalDateTime eventDate = newEventDto.getEventDate();
        checkEventDateBeforeHours(eventDate);
        checkUserExisting(userId);
        Event event = EventMapper.fromNewEventDto(newEventDto);
        return createEventFullDto(eventsRepository.save(event));
    }

    @Override
    public EventFullDto getEventById(Long userId, Long eventId) {
        Event event = getEventWithCheck(eventId);
        checkUserRights(userId, event);
        return createEventFullDto(event);
    }

    @Override
    public EventFullDto updateEvent(UpdateEventRequestParams updateEventRequestParams) {
        Long userId = updateEventRequestParams.getUserId();
        Long eventId = updateEventRequestParams.getEventId();
        UpdateEventUserRequest updateEventUserRequest = updateEventRequestParams.getUpdateEventUserRequest();

        Event event = getEventWithCheck(eventId);
        checkUserRights(userId, event);

        if (!canUserUpdateEvent(event)) {
            throw new IllegalArgumentException("Only pending or canceled events can be changed");
        }

        if (updateEventUserRequest.getEventDate() != null) {
            checkEventDateBeforeHours(updateEventUserRequest.getEventDate());
            event.setEventDate(updateEventUserRequest.getEventDate());
        }

        if (updateEventUserRequest.getCategory() != null) {
            event.setCategory(getCategoryWithCheck(updateEventUserRequest.getCategory()));
        }

        if (updateEventUserRequest.getTitle() != null) {
            event.setTitle(updateEventUserRequest.getTitle());
        }

        if (updateEventUserRequest.getDescription() != null) {
            event.setDescription(updateEventUserRequest.getDescription());
        }

        if (updateEventUserRequest.getAnnotation() != null) {
            event.setAnnotation(updateEventUserRequest.getAnnotation());
        }

        if (updateEventUserRequest.getLocation() != null) {
            Location location = updateEventUserRequest.getLocation();
            event.setLocationLat(location.getLat());
            event.setLocationLon(location.getLon());
        }

        if (updateEventUserRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateEventUserRequest.getRequestModeration());
        }

        if (updateEventUserRequest.getPaid() != null) {
            event.setPaid(updateEventUserRequest.getPaid());
        }

        if (updateEventUserRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventUserRequest.getParticipantLimit());
        }








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

    private Event getEventWithCheck(long eventId) {
        return eventsRepository.findById(eventId)
                .orElseThrow(()-> new NotFoundException(String.format("Ивент с id=%d не найден.", eventId)));
    }

    private void checkUserRights(long userId, Event event) {
        if (!event.getInitiator().getId().equals(userId)) {
            throw new ru.practicum.ewm.exception.IllegalArgumentException(
                    String.format("Юзер id=%d не имеет доступа к событию id=%d.", userId, event.getId())
            );
        }
    }

    private boolean canUserUpdateEvent(Event event) {
        EventPublishState state = event.getEventPublishState();
        return state.equals(EventPublishState.CANCELED) || state.equals(EventPublishState.PENDING);
    }

    // TODO Implement
    private User getUserWithCheck(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Юзер id=%d не найден.", userId)));
    }


    private void checkUserExisting(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("Юзер id=%d не найден.", userId));
        }
    }

    private Category getCategoryWithCheck(long categoryId) {
        return null;
    }

    private EventFullDto createEventFullDto(Event event) {
        EventDtoParams eventFullDtoParams = EventDtoParams.builder()
                .event(event)
                .categoryDto(CategoryMapper.toCategoryDto(event.getCategory()))
                .initiator(UserMapper.toUserShortDto(event.getInitiator()))
                .confirmedRequests(0L)
                .views(0L)
                .build();
        return EventMapper.toEventFullDto(eventFullDtoParams);
    }

    private EventShortDto createEventShortDto(Event event) {
        EventDtoParams eventDtoParams = EventDtoParams.builder()
                .event(event)
                .categoryDto(CategoryMapper.toCategoryDto(event.getCategory()))
                .initiator(UserMapper.toUserShortDto(event.getInitiator()))
                .confirmedRequests(0L)
                .views(0L)
                .build();

        return EventMapper.toEventShortDto(eventDtoParams);
    }

    private Pageable createPageableObject(int from, int size) {
        if (from < 0 || size <= 0) {
            throw new IllegalArgumentException("""
                    Parameters 'from' and 'size' can not be less then zero
                    """);
        }

        return PageRequest.of(from / size, size);
    }

    private static void checkEventDateBeforeHours(LocalDateTime eventDateTime) {
        LocalDateTime now = Util.getNowTruncatedToSeconds();

        if (eventDateTime.isBefore(now.plusHours(2))) {
            throw new EventCreationException(
                    String.format(
                            "Field: eventDate. Error: должно содержать дату-вермя, не ранее чем через 2 часа. Value: %s",
                            eventDateTime
                    )
            );
        }
    }
}
