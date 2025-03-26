package ru.practicum.ewm.events.mapper;

import ru.practicum.ewm.category.mapper.CategoryMapper;
import ru.practicum.ewm.events.dto.EventFullDto;
import ru.practicum.ewm.events.dto.EventShortDto;
import ru.practicum.ewm.events.dto.NewEventDto;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.user.mapper.UserMapper;

public class EventMapper {

    public static EventFullDto toEventFullDto(Event event) {

        return EventFullDto.builder()
                .id(event.getId())
                .title(event.getTitle())
                .views(event.getViews())
                .participantLimit(event.getParticipantLimit())
                .category(CategoryMapper.toCategoryDto(event.getCategory()))
                .description(event.getDescription())
                .initiator(UserMapper.toUserShortDto(event.getInitiator()))
                .annotation(event.getAnnotation())
                .createdOn(event.getCreatedOn())
                .eventDate(event.getEventDate())
                .publishedOn(event.getPublishedOn())
                .requestModeration(event.isRequestModeration())
                .confirmedRequests(event.getConfirmedRequests())
                .state(event.getState())
                .location(event.getLocation())
                .paid(event.isPaid())
                .build();
    }


    public static EventShortDto toEventShortDto(Event event) {

        return EventShortDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .title(event.getTitle())
                .views(event.getViews())
                .categoryDto(CategoryMapper.toCategoryDto(event.getCategory()))
                .confirmedRequests(event.getConfirmedRequests())
                .initiator(UserMapper.toUserShortDto(event.getInitiator()))
                .eventDate(event.getEventDate())
                .paid(event.isPaid())
                .build();
    }

    public static Event toEventEntity(NewEventDto newEventDto) {

        return Event.builder()
                .title(newEventDto.getTitle())
                .description(newEventDto.getDescription())
                .annotation(newEventDto.getAnnotation())
                //.category(Category.builder().id(newEventDto.getCategory()).build())
                .location(newEventDto.getLocation())
                //.requestModeration(newEventDto.isRequestModeration())
                //.paid(newEventDto.isPaid())
                //.participantLimit(newEventDto.getParticipantLimit())
                .eventDate(newEventDto.getEventDate())
                .build();
    }
}