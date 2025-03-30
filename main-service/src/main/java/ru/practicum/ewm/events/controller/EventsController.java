package ru.practicum.ewm.events.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.StatHitDto;
import ru.practicum.ewm.client.StatClient;
import ru.practicum.ewm.events.constants.EventsConstants;
import ru.practicum.ewm.events.dto.EventFullDto;
import ru.practicum.ewm.events.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.events.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.events.dto.EventShortDto;
import ru.practicum.ewm.events.dto.NewEventDto;
import ru.practicum.ewm.events.dto.UpdateEventAdminRequest;
import ru.practicum.ewm.events.dto.UpdateEventUserRequest;
import ru.practicum.ewm.events.dto.parameters.EventsForUserParameters;
import ru.practicum.ewm.events.dto.parameters.SearchEventsParameters;
import ru.practicum.ewm.events.dto.parameters.SearchPublicEventsParameters;
import ru.practicum.ewm.events.dto.parameters.UpdateEventParameters;
import ru.practicum.ewm.events.dto.parameters.UpdateRequestsStatusParameters;
import ru.practicum.ewm.events.enums.SortingEvents;
import ru.practicum.ewm.events.service.EventsService;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.util.Util;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@Slf4j
public class EventsController {
    private final EventsService eventsService;
    private final StatClient statClient;
    private final String applicationName;

    @Autowired
    public EventsController(EventsService eventsService,
                            StatClient statClient,
                            @Value("${application.name}")
                            String applicationName) {
        this.eventsService = eventsService;
        this.statClient = statClient;
        this.applicationName = applicationName;
    }

    // region PRIVATE
    @GetMapping(EventsConstants.PRIVATE_API_PREFIX + "/{userId}/events")
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> getEventsCreatedByUser(@PathVariable Long userId,
                                       @RequestParam(defaultValue = "0") Integer from,
                                       @RequestParam(defaultValue = "10") Integer size) {
        log.info("Request: get events for user id={}, from={}, size={}", userId, from, size);
        EventsForUserParameters eventsForUserRequestParams = EventsForUserParameters.builder()
                .userId(userId)
                .from(from)
                .size(size)
                .build();
        return eventsService.getEventsCreatedByUser(eventsForUserRequestParams);
    }

    @PostMapping(EventsConstants.PRIVATE_API_PREFIX + "/{userId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createEvent(@PathVariable Long userId,
                                    @Valid @RequestBody NewEventDto newEventDto) {
        log.info("Request: create new event from user id={}, newEventDto={}", userId, newEventDto);
        return eventsService.createEvent(userId, newEventDto);
    }

    @GetMapping(EventsConstants.PRIVATE_API_PREFIX + "/{userId}/events/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto getEventById(@PathVariable Long userId,
                                     @PathVariable Long eventId) {
        log.info("Request: get event id={} for user id={}", eventId, userId);
        return eventsService.getEventById(userId, eventId);
    }

    @PatchMapping(EventsConstants.PRIVATE_API_PREFIX + "/{userId}/events/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto updateEvent(@PathVariable Long userId,
                                    @PathVariable Long eventId,
                                    @Valid @RequestBody UpdateEventUserRequest updateEventUserRequest) {
        log.info("Request: update event id={} by user id={}, data={}", eventId, userId, updateEventUserRequest);
        UpdateEventParameters updateEventParameters = UpdateEventParameters.builder()
                .userId(userId)
                .eventId(eventId)
                .updateEventUserRequest(updateEventUserRequest)
                .build();

        return eventsService.updateEvent(updateEventParameters);
    }

    @GetMapping(EventsConstants.PRIVATE_API_PREFIX + "/{userId}/events/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequestDto> getRequestsForEvent(@PathVariable Long userId,
                                                             @PathVariable Long eventId) {
        log.info("Request: get requests for event id={} for user id={}", eventId, userId);
        return eventsService.getRequestsForEvent(userId, eventId);
    }

    @PatchMapping(EventsConstants.PRIVATE_API_PREFIX + "/{userId}/events/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public EventRequestStatusUpdateResult updateRequestsForEvent(@PathVariable Long userId,
                                                         @PathVariable Long eventId,
                                                         @RequestBody EventRequestStatusUpdateRequest updateRequest) {
        log.info("Request: update requests for event id={} for user id={}, data={}", eventId, userId, updateRequest);
        UpdateRequestsStatusParameters updateRequestsStatusParameters
                = UpdateRequestsStatusParameters.builder()
                .userId(userId)
                .eventId(eventId)
                .eventRequestStatusUpdateRequest(updateRequest)
                .build();
        return eventsService.updateRequestsForEvent(updateRequestsStatusParameters);
    }
    // endregion

    // region ADMIN
    @GetMapping(EventsConstants.ADMIN_API_PREFIX)
    @ResponseStatus(HttpStatus.OK)
    public List<EventFullDto> searchEvents(
            @RequestParam(required = false) List<Long> users,
            @RequestParam(required = false) List<String> states,
            @RequestParam(required = false) List<Long> categories,

            @DateTimeFormat(pattern = EventsConstants.DATA_TIME_FORMAT)
            @RequestParam(required = false) LocalDateTime rangeStart,

            @DateTimeFormat(pattern = EventsConstants.DATA_TIME_FORMAT)
            @RequestParam(required = false) LocalDateTime rangeEnd,

            @RequestParam(required = false, defaultValue = "0") Integer from,
            @RequestParam(required = false, defaultValue = "10") Integer size) {
        SearchEventsParameters searchEventsParameters = SearchEventsParameters.builder()
                .users(users)
                .states(states)
                .categories(categories)
                .rangeStart(rangeStart)
                .rangeEnd(rangeEnd)
                .from(from)
                .size(size)
                .build();
        log.info("Request: search events. Query={}", searchEventsParameters);
        return eventsService.searchEvents(searchEventsParameters);
    }

    @PatchMapping(EventsConstants.ADMIN_API_PREFIX + "/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto updateEventByAdmin(@PathVariable Long eventId,
                                           @RequestBody UpdateEventAdminRequest updateEventAdminRequest) {
        log.info("Request: update event id={} by admin, data={}", eventId, updateEventAdminRequest);
        return eventsService.updateEventByAdmin(eventId, updateEventAdminRequest);
    }
    // endregion

    // region PUBLIC
    @GetMapping(EventsConstants.PUBLIC_API_PREFIX)
    @ResponseStatus(HttpStatus.OK)
    public List<EventFullDto> searchPublicEvents(
            @RequestParam(required = false) String text,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) Boolean paid,

            @DateTimeFormat(pattern = EventsConstants.DATA_TIME_FORMAT)
            @RequestParam(required = false) LocalDateTime rangeStart,

            @DateTimeFormat(pattern = EventsConstants.DATA_TIME_FORMAT)
            @RequestParam(required = false) LocalDateTime rangeEnd,

            @RequestParam(required = false, defaultValue = "false") Boolean onlyAvailable,
            @RequestParam(required = false) SortingEvents sort,
            @RequestParam(required = false, defaultValue = "0") Integer from,
            @RequestParam(required = false, defaultValue = "10") Integer size,
            HttpServletRequest request) {
        SearchPublicEventsParameters searchPublicEventsParameters = SearchPublicEventsParameters.builder()
                .text(text)
                .categories(categories)
                .paid(paid)
                .rangeStart(rangeStart)
                .rangeEnd(rangeEnd)
                .from(from)
                .onlyAvailable(onlyAvailable)
                .sort(sort)
                .size(size)
                .build();
        log.info("Request: search public events. Query={}", searchPublicEventsParameters);
        List<EventFullDto> result = eventsService.searchPublicEvents(searchPublicEventsParameters);
        hitStat(request);
        return result;
    }

    @GetMapping(EventsConstants.PUBLIC_API_PREFIX + "/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto getPublicEventById(@PathVariable Long eventId, HttpServletRequest request) {
        log.info("Request: get public event with id={}", eventId);
        EventFullDto result = eventsService.getPublicEventById(eventId);
        hitStat(request);
        return result;
    }
    // endregion

    private void hitStat(HttpServletRequest request) {
        StatHitDto statHitDto = StatHitDto.builder()
                .app(applicationName)
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(Util.getNowTruncatedToSeconds())
                .build();
        try {
            statClient.hit(statHitDto);
        } catch (Exception e) {
            log.error("Error on hitting stats. Msg: {}, \nstackTrace: {}", e.getMessage(), e.getStackTrace());
        }
    }
}
