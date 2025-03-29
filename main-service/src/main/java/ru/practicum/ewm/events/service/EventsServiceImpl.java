package ru.practicum.ewm.events.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.mapper.CategoryMapper;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.storage.CategoryRepository;
import ru.practicum.ewm.error.exception.DataIntegrityViolationException;
import ru.practicum.ewm.error.exception.NotFoundException;
import ru.practicum.ewm.error.exception.ValidationException;
import ru.practicum.ewm.events.dto.EventFullDto;
import ru.practicum.ewm.events.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.events.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.events.dto.EventShortDto;
import ru.practicum.ewm.events.dto.NewEventDto;
import ru.practicum.ewm.events.dto.UpdateEventAdminRequest;
import ru.practicum.ewm.events.dto.UpdateEventCommonRequest;
import ru.practicum.ewm.events.dto.UpdateEventUserRequest;
import ru.practicum.ewm.events.dto.parameters.EventsForUserRequestParams;
import ru.practicum.ewm.events.dto.parameters.SearchEventsRequestParams;
import ru.practicum.ewm.events.dto.parameters.SearchPublicEventsRequestParams;
import ru.practicum.ewm.events.dto.parameters.UpdateEventRequestParams;
import ru.practicum.ewm.events.dto.parameters.UpdateRequestsStatusRequestParams;
import ru.practicum.ewm.events.mapper.EventDtoParams;
import ru.practicum.ewm.events.mapper.EventMapper;
import ru.practicum.ewm.events.model.AdminEventAction;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.events.model.EventPublishState;
import ru.practicum.ewm.events.model.Location;
import ru.practicum.ewm.events.model.QEvent;
import ru.practicum.ewm.events.model.SortingEvents;
import ru.practicum.ewm.events.model.UserUpdateRequestAction;
import ru.practicum.ewm.events.storage.EventsRepository;
import ru.practicum.ewm.error.exception.ConflictException;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.mapper.RequestMapper;
import ru.practicum.ewm.request.model.Request;
import ru.practicum.ewm.request.model.RequestStatus;
import ru.practicum.ewm.request.repository.RequestRepository;
import ru.practicum.ewm.user.mapper.UserMapper;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;
import ru.practicum.ewm.util.Util;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
@Transactional
public class EventsServiceImpl implements EventsService {
    private final EventsRepository eventsRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final RequestRepository requestRepository;

    private final EventsViewsService eventsViewsService;

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getEventsCreatedByUser(EventsForUserRequestParams eventsForUserRequestParams) {
        Long userId = eventsForUserRequestParams.getUserId();
        Integer from = eventsForUserRequestParams.getFrom();
        Integer size = eventsForUserRequestParams.getSize();

        checkUserExisting(userId);

        Pageable page = createPageableObject(from, size);
        List<Event> userEvents = eventsRepository.findAllByInitiatorIdIs(userId, page).stream()
                .toList();

        return createEventShortDtoList(userEvents);
    }

    @Override
    public EventFullDto createEvent(Long userId, NewEventDto newEventDto) {
        checkEventDateBeforeHours(newEventDto.getEventDate());
        User user = getUserWithCheck(userId);
        Category category = getCategoryWithCheck(newEventDto.getCategory());
        Event event = EventMapper.fromNewEventDto(newEventDto, category);
        event.setCreatedOn(Util.getNowTruncatedToSeconds());
        event.setInitiator(user);
        return createEventFullDto(eventsRepository.save(event));
    }

    @Override
    @Transactional(readOnly = true)
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
            throw new DataIntegrityViolationException("Only pending or canceled events can be changed");
        }

        UpdateEventCommonRequest commonRequest = EventMapper.userUpdateRequestToCommonRequest(updateEventUserRequest);
        updateCommonEventProperties(event, commonRequest);

        if (updateEventUserRequest.getStateAction() != null) {
            switch (updateEventUserRequest.getStateAction()) {
                case CANCEL_REVIEW -> event.setEventPublishState(EventPublishState.CANCELED);
                case SEND_TO_REVIEW -> event.setEventPublishState(EventPublishState.PENDING);
            }
        }

        return createEventFullDto(eventsRepository.save(event));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getRequestsForEvent(Long userId, Long eventId) {
        Event event = getEventWithCheck(eventId);
        checkUserRights(userId, event);
        List<Request> requests = requestRepository.findByEventId(eventId);
        return requests.stream()
                .map(RequestMapper::toRequestDto)
                .toList();
    }

    @Override
    public EventRequestStatusUpdateResult updateRequestsForEvent(UpdateRequestsStatusRequestParams updateParams) {
        Long userId = updateParams.getUserId();
        Long eventId = updateParams.getEventId();
        EventRequestStatusUpdateRequest statusUpdateRequest = updateParams.getEventRequestStatusUpdateRequest();

        Event event = getEventWithCheck(eventId);
        checkUserRights(userId, event);

        EventRequestStatusUpdateResult result = EventRequestStatusUpdateResult.builder()
                .confirmedRequests(new ArrayList<>())
                .rejectedRequests(new ArrayList<>())
                .build();

        UserUpdateRequestAction action = statusUpdateRequest.getStatus();
        List<Request> requests = requestRepository.findAllById(statusUpdateRequest.getRequestIds());
        Long confirmedRequests = getConfirmedRequestsMap(List.of(eventId)).get(eventId);
        Integer participantLimit = event.getParticipantLimit();

        long canConfirmRequestsNumber = participantLimit == 0
                ? requests.size()
                : participantLimit - confirmedRequests;

        if (canConfirmRequestsNumber <= 0) {
            throw new DataIntegrityViolationException(String.format(
                    "Event id=%d is full for requests.", eventId
            ));
        }

        requests.forEach(request -> {
            if (request.getStatus() != RequestStatus.PENDING) {
                throw new DataIntegrityViolationException(String.format(
                        "Request id=%d must have status PENDING.", request.getId()
                ));
            }
        });

        for (Request request : requests) {
            if (action == UserUpdateRequestAction.REJECTED || canConfirmRequestsNumber <= 0) {
                request.setStatus(RequestStatus.REJECTED);
                result.getRejectedRequests().add(RequestMapper.toRequestDto(request));
                continue;
            }

            request.setStatus(RequestStatus.CONFIRMED);
            result.getConfirmedRequests().add(RequestMapper.toRequestDto(request));
            canConfirmRequestsNumber--;
        }

        requestRepository.saveAll(requests);
        return result;
    }

    @Override
    @Transactional(readOnly = true)
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

        BooleanExpression condition = conditions.stream()
                .reduce(Expressions.asBoolean(true).isTrue(), BooleanExpression::and);
        List<Event> resultEvents = eventsRepository.findAll(condition, page).stream()
                .toList();
        return createEventFullDtoList(resultEvents);
    }

    @Override
    public EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest updateRequest) {
        Event event = getEventWithCheck(eventId);
        UpdateEventCommonRequest commonRequest = EventMapper.adminUpdateRequestToCommonRequest(updateRequest);
        updateCommonEventProperties(event, commonRequest);

        if (updateRequest.getStateAction() != null) {
            AdminEventAction stateAction = updateRequest.getStateAction();
            EventPublishState eventPublishState = event.getEventPublishState();

            if (stateAction == AdminEventAction.REJECT_EVENT) {
                if (eventPublishState == EventPublishState.PUBLISHED) {
                    throw new DataIntegrityViolationException("Нельзя отменить опубликованное событие.");
                }

                event.setEventPublishState(EventPublishState.CANCELED);
            } else if (stateAction == AdminEventAction.PUBLISH_EVENT) {
                if (eventPublishState != EventPublishState.PENDING) {
                    throw new DataIntegrityViolationException("Опубликовать можно только то событие, которое ожидает публикации.");
                }

                LocalDateTime now = Util.getNowTruncatedToSeconds();

                if (now.plusHours(1).isAfter(event.getEventDate())) {
                    throw new DataIntegrityViolationException("Нельзя опубликовать событие, до которого осталось менее 1 часа.");
                }

                event.setEventPublishState(EventPublishState.PUBLISHED);
                event.setPublishedOn(now);
            }
        }

        return createEventFullDto(eventsRepository.save(event));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventFullDto> searchPublicEvents(SearchPublicEventsRequestParams searchParams) {
        QEvent event = QEvent.event;
        List<BooleanExpression> conditions = new ArrayList<>();
        conditions.add(event.eventPublishState.eq(EventPublishState.PUBLISHED));

        if (searchParams.getText() != null) {
            String text = searchParams.getText();
            conditions.add(event.annotation.containsIgnoreCase(text).or(event.description.containsIgnoreCase(text)));
        }

        if (searchParams.getCategories() != null) {
            List<Category> categories = categoryRepository.findAllById(searchParams.getCategories());

            if (categories.isEmpty()) {
                throw new ValidationException("Категории для поиска не существуют.");
            }

            conditions.add(event.category.id.in(searchParams.getCategories()));
        }

        if (searchParams.getOnlyAvailable() != null) {
            List<Long> ids = eventsRepository.getAvailableEventIdsByParticipantLimit();
            conditions.add(event.id.in(ids));
        }

        if (searchParams.getPaid() != null) {
            conditions.add(event.paid.eq(searchParams.getPaid()));
        }

        if (searchParams.getRangeStart() != null || searchParams.getRangeEnd() != null) {
            if (searchParams.getRangeStart() != null) {
                conditions.add(event.eventDate.after(searchParams.getRangeStart()));
            }

            if (searchParams.getRangeEnd() != null) {
                conditions.add(event.eventDate.before(searchParams.getRangeEnd()));
            }
        } else {
            LocalDateTime now = Util.getNowTruncatedToSeconds();
            conditions.add(event.eventDate.after(now));
        }

        BooleanExpression condition = conditions.stream()
                .reduce(Expressions.asBoolean(true).isTrue(), BooleanExpression::and);
        Iterable<Event> resultEvents = eventsRepository.findAll(condition);
        List<Long> resultEventIds = StreamSupport.stream(resultEvents.spliterator(), false)
                .map(Event::getId)
                .toList();
        Map<Long, Long> eventsViewsMap = eventsViewsService.getEventsViewsMap(resultEventIds);
        Map<Long, Long> confirmedRequestsMap = getConfirmedRequestsMap(resultEventIds);
        Comparator<Event> sorting = searchParams.getSort() == SortingEvents.VIEWS
                ? (ev1, ev2) -> Long.compare(eventsViewsMap.get(ev2.getId()), eventsViewsMap.get(ev1.getId()))
                : Comparator.comparing(Event::getEventDate);

        return StreamSupport.stream(resultEvents.spliterator(), false)
                .sorted(sorting)
                .skip(searchParams.getFrom())
                .limit(searchParams.getSize())
                .map(ev -> createEventFullDto(ev, eventsViewsMap.get(ev.getId()), confirmedRequestsMap.get(ev.getId())))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto getPublicEventById(Long eventId) {
        QEvent event = QEvent.event;
        Event resultEvent = eventsRepository
                .findOne(event.id.eq(eventId).and(event.eventPublishState.eq(EventPublishState.PUBLISHED)))
                .orElseThrow(() -> new NotFoundException(
                        String.format("Событие с id=%d не найдено или не является опубликованным.", eventId))
                );
        return createEventFullDto(resultEvent);
    }

    private void updateCommonEventProperties(Event event, UpdateEventCommonRequest commonProperties) {
        if (commonProperties.getEventDate() != null) {
            checkEventDateBeforeHours(commonProperties.getEventDate());
            event.setEventDate(commonProperties.getEventDate());
        }

        if (commonProperties.getCategory() != null) {
            event.setCategory(getCategoryWithCheck(commonProperties.getCategory()));
        }

        if (commonProperties.getTitle() != null) {
            event.setTitle(commonProperties.getTitle());
        }

        if (commonProperties.getDescription() != null) {
            event.setDescription(commonProperties.getDescription());
        }

        if (commonProperties.getAnnotation() != null) {
            event.setAnnotation(commonProperties.getAnnotation());
        }

        if (commonProperties.getLocation() != null) {
            Location location = commonProperties.getLocation();
            event.setLocationLat(location.getLat());
            event.setLocationLon(location.getLon());
        }

        if (commonProperties.getRequestModeration() != null) {
            event.setRequestModeration(commonProperties.getRequestModeration());
        }

        if (commonProperties.getPaid() != null) {
            event.setPaid(commonProperties.getPaid());
        }

        if (commonProperties.getParticipantLimit() != null) {
            event.setParticipantLimit(commonProperties.getParticipantLimit());
        }
    }

    private Event getEventWithCheck(long eventId) {
        return eventsRepository.findById(eventId)
                .orElseThrow(() -> new ConflictException(String.format("Ивент с id=%d не найден.", eventId)));
    }

    private void checkUserRights(long userId, Event event) {
        if (!event.getInitiator().getId().equals(userId)) {
            throw new ConflictException(
                    String.format("Юзер id=%d не имеет доступа к событию id=%d.", userId, event.getId())
            );
        }
    }

    private Map<Long, Long> getConfirmedRequestsMap(List<Long> eventIds) {
        return eventsRepository.getConfirmedRequestsForEvents(eventIds).stream()
                .collect(Collectors.toMap(List::getFirst, List::getLast));
    }

    private boolean canUserUpdateEvent(Event event) {
        EventPublishState state = event.getEventPublishState();
        return state.equals(EventPublishState.CANCELED) || state.equals(EventPublishState.PENDING);
    }

    private void checkUserExisting(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ConflictException(String.format("Юзер id=%d не найден.", userId));
        }
    }

    private User getUserWithCheck(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ConflictException(String.format("Юзер id=%d не найден.", userId)));
    }

    private Category getCategoryWithCheck(long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ConflictException(String.format("Категория id=%d не найдена.", categoryId)));
    }

    private EventFullDto createEventFullDto(Event event) {
        long id = event.getId();
        Map<Long, Long> eventsViewsMap = eventsViewsService.getEventsViewsMap(List.of(id));
        Map<Long, Long> confirmedRequestsMap = getConfirmedRequestsMap(List.of(id));

        EventDtoParams eventFullDtoParams = EventDtoParams.builder()
                .event(event)
                .categoryDto(CategoryMapper.toCategoryDto(event.getCategory()))
                .initiator(UserMapper.toUserShortDto(event.getInitiator()))
                .confirmedRequests(confirmedRequestsMap.get(id))
                .views(eventsViewsMap.get(id))
                .build();
        return EventMapper.toEventFullDto(eventFullDtoParams);
    }

    private EventFullDto createEventFullDto(Event event, long views, long confirmedRequests) {
        EventDtoParams eventFullDtoParams = EventDtoParams.builder()
                .event(event)
                .categoryDto(CategoryMapper.toCategoryDto(event.getCategory()))
                .initiator(UserMapper.toUserShortDto(event.getInitiator()))
                .confirmedRequests(confirmedRequests)
                .views(views)
                .build();
        return EventMapper.toEventFullDto(eventFullDtoParams);
    }

    private List<EventFullDto> createEventFullDtoList(List<Event> events) {
        List<Long> ids = events.stream()
                .map(Event::getId)
                .toList();
        Map<Long, Long> eventsViewsMap = eventsViewsService.getEventsViewsMap(ids);
        Map<Long, Long> confirmedRequestsMap = getConfirmedRequestsMap(ids);

        return events.stream()
                .map(event -> {
                    EventDtoParams eventFullDtoParams = EventDtoParams.builder()
                            .event(event)
                            .categoryDto(CategoryMapper.toCategoryDto(event.getCategory()))
                            .initiator(UserMapper.toUserShortDto(event.getInitiator()))
                            .confirmedRequests(confirmedRequestsMap.get(event.getId()))
                            .views(eventsViewsMap.get(event.getId()))
                            .build();
                    return EventMapper.toEventFullDto(eventFullDtoParams);
                })
                .toList();
    }

    private List<EventShortDto> createEventShortDtoList(List<Event> events) {
        List<Long> ids = events.stream()
                .map(Event::getId)
                .toList();
        Map<Long, Long> eventsViewsMap = eventsViewsService.getEventsViewsMap(ids);
        Map<Long, Long> confirmedRequestsMap = getConfirmedRequestsMap(ids);

        return events.stream()
                .map(event -> {
                    EventDtoParams eventDtoParams = EventDtoParams.builder()
                            .event(event)
                            .categoryDto(CategoryMapper.toCategoryDto(event.getCategory()))
                            .initiator(UserMapper.toUserShortDto(event.getInitiator()))
                            .confirmedRequests(confirmedRequestsMap.get(event.getId()))
                            .views(eventsViewsMap.get(event.getId()))
                            .build();

                    return EventMapper.toEventShortDto(eventDtoParams);
                })
                .toList();
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
            throw new ValidationException(
                    String.format(
                            "Field: eventDate. Error: должно содержать дату-вермя, не ранее чем через 2 часа. Value: %s",
                            eventDateTime
                    )
            );
        }
    }
}
