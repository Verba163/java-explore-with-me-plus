package ru.practicum.ewm.events.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.ewm.events.dto.EventFullDto;
import ru.practicum.ewm.events.dto.EventShortDto;
import ru.practicum.ewm.events.dto.NewEventDto;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.events.model.Location;

@Component
public class EventMapper {
    public static Event fromNewEventDto(NewEventDto newEventDto) {
        return Event.builder()
                .build();
    }

    public static EventFullDto toEventFullDto(EventDtoParams eventFullDtoParams) {
        Event event = eventFullDtoParams.getEvent();

        return EventFullDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(eventFullDtoParams.getCategoryDto())
                .confirmedRequests(eventFullDtoParams.getConfirmedRequests())
                .createdOn(event.getCreatedOn())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .initiator(eventFullDtoParams.getInitiator())
                .location(new Location(event.getLocationLat(), event.getLocationLon()))
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(event.getPublishedOn())
                .requestModeration(event.getRequestModeration())
                .title(event.getTitle())
                .state(event.getEventPublishState())
                .views(eventFullDtoParams.getViews())
                .build();
    }

    public static EventShortDto toEventShortDto(EventDtoParams eventDtoParams) {
        Event event = eventDtoParams.getEvent();

        return EventShortDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(eventDtoParams.getCategoryDto())
                .confirmedRequests(eventDtoParams.getConfirmedRequests())
                .eventDate(event.getEventDate())
                .initiator(eventDtoParams.getInitiator())
                .paid(event.getPaid())
                .title(event.getTitle())
                .views(eventDtoParams.getViews())
                .build();
    }

//    public class EventFullDto {
//        private Long id;
//        private String annotation;
//        private CategoryDto category;
//        private Long confirmedRequests;
//        private LocalDateTime createdOn;
//        private String description;
//        private LocalDateTime eventDate;
//        private UserShortDto initiator;
//        private Location location;
//        private Boolean paid;
//        private Long participantLimit;
//        private LocalDateTime publishedOn;
//        private Boolean requestModeration;
//        private String title;
//        private EventPublishState state;
//        private Long views;
//    }


//    public static EventFullDto toEventFullDto(Event event) {
//
//        return EventFullDto.builder()
//                .id(event.getId())
//                .title(event.getTitle())
//                .views(event.getViews())
//                .participantLimit(event.getParticipantLimit())
//                .category(CategoryMapper.toCategoryDto(event.getCategory()))
//                .description(event.getDescription())
//                .initiator(UserMapper.toUserShortDto(event.getInitiator()))
//                .annotation(event.getAnnotation())
//                .createdOn(event.getCreatedOn())
//                .eventDate(event.getEventDate())
//                .publishedOn(event.getPublishedOn())
//                .requestModeration(event.isRequestModeration())
//                .confirmedRequests(event.getConfirmedRequests())
//                .eventPublishState(event.getEventPublishState())
//                .location(event.getLocation())
//                .paid(event.isPaid())
//                .build();
//    }
//
//
//    public static EventShortDto toEventShortDto(Event event) {
//
//        return EventShortDto.builder()
//                .id(event.getId())
//                .annotation(event.getAnnotation())
//                .title(event.getTitle())
//                .views(event.getViews())
//                .categoryDto(CategoryMapper.toCategoryDto(event.getCategory()))
//                .confirmedRequests(event.getConfirmedRequests())
//                .initiator(UserMapper.toUserShortDto(event.getInitiator()))
//                .eventDate(event.getEventDate())
//                .paid(event.isPaid())
//                .build();
//    }
//
//    public static Event toEventEntity(NewEventDto newEventDto) {
//
//        return Event.builder()
//                .title(newEventDto.getTitle())
//                .description(newEventDto.getDescription())
//                .annotation(newEventDto.getAnnotation())
//                //.category(Category.builder().id(newEventDto.getCategory()).build())
//                .location(newEventDto.getLocation())
//                //.requestModeration(newEventDto.isRequestModeration())
//                //.paid(newEventDto.isPaid())
//                //.participantLimit(newEventDto.getParticipantLimit())
//                .eventDate(newEventDto.getEventDate())
//                .build();
//    }
}