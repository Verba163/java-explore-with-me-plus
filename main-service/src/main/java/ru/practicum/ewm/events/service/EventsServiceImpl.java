package ru.practicum.ewm.events.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.category.mapper.CategoryMapper;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.storage.CategoryRepository;
import ru.practicum.ewm.events.dto.EventFullDto;
import ru.practicum.ewm.events.dto.EventRequestStatusUpdateRequest;
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
import ru.practicum.ewm.events.model.AdminAction;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.events.model.EventPublishState;
import ru.practicum.ewm.events.model.Location;
import ru.practicum.ewm.events.model.QEvent;
import ru.practicum.ewm.events.storage.EventsRepository;
import ru.practicum.ewm.error.exception.NotFoundException;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.user.mapper.UserMapper;
import ru.practicum.ewm.user.repository.UserRepository;
import ru.practicum.ewm.util.Util;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EventsServiceImpl implements EventsService {
    private final EventsRepository eventsRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

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

        if (updateEventUserRequest.getStateAction() != null) {
            switch (updateEventUserRequest.getStateAction()) {
                case CANCEL_REVIEW -> event.setEventPublishState(EventPublishState.CANCELED);
                case SEND_TO_REVIEW -> event.setEventPublishState(EventPublishState.PENDING);
            }
        }

        return createEventFullDto(eventsRepository.save(event));
    }

    @Override
    public List<ParticipationRequestDto> getRequestsForEvent(Long userId, Long eventId) {
        Event event = getEventWithCheck(eventId);
        checkUserRights(userId, event);

        // TODO get requests

        return List.of();
    }

    @Override
    public EventRequestStatusUpdateResult updateRequestsForEvent(UpdateRequestsStatusRequestParams updateParams) {
        Long userId = updateParams.getUserId();
        Long eventId = updateParams.getEventId();
        EventRequestStatusUpdateRequest statusUpdateRequest = updateParams.getEventRequestStatusUpdateRequest();

        Event event = getEventWithCheck(eventId);
        checkUserRights(userId, event);

        // TODO process request

        return null;
    }

    @Override
    public List<EventFullDto> searchEvents(SearchEventsRequestParams searchParams) {
        QEvent event = QEvent.event;
        List<BooleanExpression> conditions = new ArrayList<>();
        Pageable page = createPageableObject(searchParams.getFrom(), searchParams.getSize());

        if (searchParams.getUsers() != null) {
            conditions.add(event.initiator.id.in(searchParams.getUsers()));
        }

        if (searchParams.getStates() != null) {
            List<EventPublishState> states = searchParams.getStates().stream()
                    .map(EventPublishState::valueOf)
                    .toList();
            conditions.add(event.eventPublishState.in(states));
        }

        if (searchParams.getCategories() != null) {
            conditions.add(event.category.id.in(searchParams.getCategories()));
        }

        if (searchParams.getRangeStart() != null) {
            conditions.add(event.eventDate.after(searchParams.getRangeStart()));
        }

        if (searchParams.getRangeEnd() != null) {
            conditions.add(event.eventDate.before(searchParams.getRangeEnd()));
        }

        Optional<BooleanExpression> condition = conditions.stream()
                .reduce(BooleanExpression::and);

        if (condition.isEmpty()) {
            return eventsRepository.findAll(page).stream()
                    .map(this::createEventFullDto)
                    .toList();
        } else {
            return eventsRepository.findAll(condition.get(), page).stream()
                    .map(this::createEventFullDto)
                    .toList();
        }
    }

    @Override
    public EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest updateRequest) {
        Event event = getEventWithCheck(eventId);

        if (updateRequest.getEventDate() != null) {
            checkEventDateBeforeHours(updateRequest.getEventDate());
            event.setEventDate(updateRequest.getEventDate());
        }

        if (updateRequest.getStateAction() != null) {
            AdminAction stateAction = updateRequest.getStateAction();
            EventPublishState eventPublishState = event.getEventPublishState();

            if (stateAction == AdminAction.REJECT_EVENT) {
                if (eventPublishState == EventPublishState.PUBLISHED) {
                    throw new IllegalArgumentException("Нельзя отменить опубликованное событие.");
                }

                event.setEventPublishState(EventPublishState.CANCELED);
            } else if (stateAction == AdminAction.PUBLISH_EVENT) {
                if (eventPublishState != EventPublishState.PENDING) {
                    throw new IllegalArgumentException("Опубликовать можно только то событие, которое ожидает публикации.");
                }

                LocalDateTime now = Util.getNowTruncatedToSeconds();

                if (now.plusHours(1).isBefore(event.getEventDate())) {
                    throw new IllegalArgumentException("Нельзя опубликовать событие, до которого осталось менее 1 часа.");
                }

                event.setEventPublishState(EventPublishState.PUBLISHED);
                event.setPublishedOn(now);
            }
        }

        if (updateRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateRequest.getRequestModeration());
        }

        if (updateRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateRequest.getRequestModeration());
        }

        if (updateRequest.getCategory() != null) {
            event.setCategory(getCategoryWithCheck(updateRequest.getCategory()));
        }

        if (updateRequest.getTitle() != null) {
            event.setTitle(updateRequest.getTitle());
        }

        if (updateRequest.getDescription() != null) {
            event.setDescription(updateRequest.getDescription());
        }

        if (updateRequest.getAnnotation() != null) {
            event.setAnnotation(updateRequest.getAnnotation());
        }

        if (updateRequest.getLocation() != null) {
            Location location = updateRequest.getLocation();
            event.setLocationLat(location.getLat());
            event.setLocationLon(location.getLon());
        }

        if (updateRequest.getPaid() != null) {
            event.setPaid(updateRequest.getPaid());
        }

        if (updateRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateRequest.getParticipantLimit());
        }

        return createEventFullDto(eventsRepository.save(event));
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
            throw new IllegalArgumentException(
                    String.format("Юзер id=%d не имеет доступа к событию id=%d.", userId, event.getId())
            );
        }
    }

    private boolean canUserUpdateEvent(Event event) {
        EventPublishState state = event.getEventPublishState();
        return state.equals(EventPublishState.CANCELED) || state.equals(EventPublishState.PENDING);
    }

    private void checkUserExisting(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("Юзер id=%d не найден.", userId));
        }
    }

    private Category getCategoryWithCheck(long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException(String.format("Категория id=%d не найдена.", categoryId)));
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
                    """
            );
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
