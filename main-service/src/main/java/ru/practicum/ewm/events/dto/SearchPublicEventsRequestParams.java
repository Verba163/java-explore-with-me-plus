package ru.practicum.ewm.events.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Builder(toBuilder = true)
@Getter
public class SearchPublicEventsRequestParams {
    private String text;
    private List<Integer> categories;
    private Boolean paid;
    private LocalDateTime rangeStart;
    private LocalDateTime rangeEnd;
    private Boolean onlyAvailable;
    private String sort;
    private Integer from;
    private Integer size;
}
