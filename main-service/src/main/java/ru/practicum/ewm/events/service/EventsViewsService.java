package ru.practicum.ewm.events.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.dto.StatViewDto;
import ru.practicum.ewm.client.StatClient;
import ru.practicum.ewm.util.Util;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventsViewsService {
    private final StatClient statClient;

    public Map<Long, Long> getEventsViewsMap(List<Long> eventIds) {
        // id -> URI
        Map<Long, String> eventUriMap = eventIds.stream()
                .collect(Collectors.toMap(Function.identity(), this::createURIForEventId));
        List<String> uris = eventUriMap.values().stream()
                .toList();
        LocalDateTime start = LocalDateTime.MIN;
        LocalDateTime end = Util.getNowTruncatedToSeconds();
        List<StatViewDto> stat = List.of();

        try {
            stat = statClient.getStat(start, end, uris, false);
        } catch (Exception e) {
            log.error(
                    "Ошибка при попытке получить статистику. Msg: {}, \nstackTrace: {}",
                    e.getMessage(),
                    e.getStackTrace()
            );
        }

        // URI -> hits
        Map<String, Long> uriHitsMap = stat.stream()
                .collect(Collectors.toMap(StatViewDto::getUri, StatViewDto::getHits));

        // id -> hits
        return eventIds.stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        id -> uriHitsMap.getOrDefault(eventUriMap.get(id), 0L)
                ));
    }

    private String createURIForEventId(long id) {
        return String.format("/events/%d", id);
    }
}
