package ru.practicum.ewm.events;

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
import ru.practicum.ewm.events.service.EventsService;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.util.Util;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@Slf4j
public class EventsController {
    private static final String PRIVATE_API_PREFIX = "/users";
    private static final String ADMIN_API_PREFIX = "/admin/events";
    private static final String PUBLIC_API_PREFIX = "/events";

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
    @GetMapping(PRIVATE_API_PREFIX + "/{userId}/events")
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> getEventsCreatedByUser(@PathVariable Long userId,
                                       @RequestParam(defaultValue = "0") Integer from,
                                       @RequestParam(defaultValue = "10") Integer size) {
        log.info("Request: get events for user id={}, from={}, size={}", userId, from, size);
        EventsForUserRequestParams eventsForUserRequestParams = EventsForUserRequestParams.builder()
                .userId(userId)
                .from(from)
                .size(size)
                .build();
        return eventsService.getEventsCreatedByUser(eventsForUserRequestParams);
    }

    @PostMapping(PRIVATE_API_PREFIX + "/{userId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createEvent(@PathVariable Long userId,
                                    @Valid @RequestBody NewEventDto newEventDto) {
        log.info("Request: create new event from user id={}, newEventDto={}", userId, newEventDto);
        checkEventDate(newEventDto.getEventDate());
        return eventsService.createEvent(userId, newEventDto);
    }

    @GetMapping(PRIVATE_API_PREFIX + "/{userId}/events/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto getEventById(@PathVariable Long userId,
                                     @PathVariable Long eventId) {
        log.info("Request: get event id={} for user id={}", eventId, userId);
        return eventsService.getEventById(userId, eventId);
    }

    @PatchMapping(PRIVATE_API_PREFIX + "/{userId}/events/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto updateEvent(@PathVariable Long userId,
                                    @PathVariable Long eventId,
                                    @Valid @RequestBody UpdateEventUserRequest updateEventUserRequest) {
        log.info("Request: update event id={} by user id={}, data={}", eventId, userId, updateEventUserRequest);

        if (updateEventUserRequest.getEventDate() != null) {
            checkEventDate(updateEventUserRequest.getEventDate());
        }

        UpdateEventRequestParams updateEventRequestParams = UpdateEventRequestParams.builder()
                .userId(userId)
                .eventId(eventId)
                .updateEventUserRequest(updateEventUserRequest)
                .build();

        return eventsService.updateEvent(updateEventRequestParams);
    }

    @GetMapping(PRIVATE_API_PREFIX + "/{userId}/events/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequestDto> getRequestsForEvent(@PathVariable Long userId,
                                                             @PathVariable Long eventId) {
        log.info("Request: get requests for event id={} for user id={}", eventId, userId);
        return eventsService.getRequestsForEvent(userId, eventId);
    }

    @PatchMapping(PRIVATE_API_PREFIX + "/{userId}/events/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public EventRequestStatusUpdateResult updateRequestsForEvent(@PathVariable Long userId,
                                                         @PathVariable Long eventId,
                                                         @RequestBody EventRequestStatusUpdateRequest updateRequest) {
        log.info("Request: update requests for event id={} for user id={}, data={}", eventId, userId, updateRequest);
        UpdateRequestsStatusRequestParams updateRequestsStatusRequestParams
                = UpdateRequestsStatusRequestParams.builder()
                .userId(userId)
                .eventId(eventId)
                .eventRequestStatusUpdateRequest(updateRequest)
                .build();
        return eventsService.updateRequestsForEvent(updateRequestsStatusRequestParams);
    }
    // endregion

    // region ADMIN
    @GetMapping(ADMIN_API_PREFIX)
    @ResponseStatus(HttpStatus.OK)
    public List<EventFullDto> searchEvents(
            @RequestParam(required = false) List<Long> users,
            @RequestParam(required = false) List<String> states,
            @RequestParam(required = false) List<Integer> categories,

            @DateTimeFormat(pattern = "${spring.jackson.date-format}")
            @RequestParam(required = false) LocalDateTime rangeStart,

            @DateTimeFormat(pattern = "${spring.jackson.date-format}")
            @RequestParam(required = false) LocalDateTime rangeEnd,

            @RequestParam(required = false, defaultValue = "0") Integer from,
            @RequestParam(required = false, defaultValue = "10") Integer size) {
        SearchEventsRequestParams searchEventsRequestParams = SearchEventsRequestParams.builder()
                .users(users)
                .states(states)
                .categories(categories)
                .rangeStart(rangeStart)
                .rangeEnd(rangeEnd)
                .from(from)
                .size(size)
                .build();
        log.info("Request: search events. Query={}", searchEventsRequestParams);
        return eventsService.searchEvents(searchEventsRequestParams);
    }

    @PatchMapping(ADMIN_API_PREFIX + "/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto updateEventByAdmin(@PathVariable Long eventId,
                                           @RequestBody UpdateEventAdminRequest updateEventAdminRequest) {
        log.info("Request: update event id={} by admin, data={}", eventId, updateEventAdminRequest);
        return eventsService.updateEventByAdmin(eventId, updateEventAdminRequest);
    }
    // endregion

    // region PUBLIC
    @GetMapping(PUBLIC_API_PREFIX)
    @ResponseStatus(HttpStatus.OK)
    public List<EventFullDto> searchPublicEvents(
            @RequestParam(required = false) String text,
            @RequestParam(required = false) List<Integer> categories,
            @RequestParam(required = false) Boolean paid,

            @DateTimeFormat(pattern = "${spring.jackson.date-format}")
            @RequestParam(required = false) LocalDateTime rangeStart,

            @DateTimeFormat(pattern = "${spring.jackson.date-format}")
            @RequestParam(required = false) LocalDateTime rangeEnd,

            @RequestParam(required = false, defaultValue = "false") Boolean onlyAvailable,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false, defaultValue = "0") Integer from,
            @RequestParam(required = false, defaultValue = "10") Integer size,
            HttpServletRequest request) {
        SearchPublicEventsRequestParams searchPublicEventsRequestParams = SearchPublicEventsRequestParams.builder()
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
        log.info("Request: search public events. Query={}", searchPublicEventsRequestParams);
        List<EventFullDto> result = eventsService.searchPublicEvents(searchPublicEventsRequestParams);
        hitStat(request);
        return result;
    }

    @GetMapping(PUBLIC_API_PREFIX + "/{eventId}")
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
            log.error("Fail to hit stat. Error: {}. \nStack trace:\n{}", e.getMessage(), e.getStackTrace());
        }
    }

    private static void checkEventDate(LocalDateTime eventDateTime) {
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
