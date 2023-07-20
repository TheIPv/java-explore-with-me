package ru.yandex.practicum.event.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.StatsClient;
import ru.yandex.practicum.dto.EndpointHitDto;
import ru.yandex.practicum.event.model.Event;
import ru.yandex.practicum.event.repository.RequestRepository;
import ru.yandex.practicum.dto.ViewStatsDto;


import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MainStatsServiceImpl implements MainStatsService {

    private final StatsClient statsClient;
    private final RequestRepository requestRepository;
    private final ObjectMapper mapper = new ObjectMapper();

    @Value(value = "${app.name}")
    private String appName;

    @Override
    public void addHit(HttpServletRequest request) {
        statsClient.saveHit(EndpointHitDto
                .builder()
                .app(appName)
                .ip(request.getRemoteAddr())
                .uri(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build());
    }

    @Override
    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        ResponseEntity<Object> response = statsClient.getStats(start.format(formatter), end.format(formatter), uris, unique);

        try {
            return List.of(mapper.readValue(mapper.writeValueAsString(response.getBody()), ViewStatsDto[].class));
        } catch (IOException exception) {
            throw new ClassCastException(exception.getMessage());
        }
    }

    @Override
    public Map<Long, Long> getViews(List<Event> events) {


        Map<Long, Long> views = new HashMap<>();

        List<Event> publishedEvents = getPublished(events);

        if (events.isEmpty()) {
            return views;
        }

        LocalDateTime start = LocalDateTime.of(2020,01,01,00,00,00);
        LocalDateTime end = LocalDateTime.now();
        List<String> uris = publishedEvents.stream()
                .map(Event::getId)
                .map(id -> ("/events/" + id))
                .collect(Collectors.toList());

        List<ViewStatsDto> stats = getStats(start, end, uris, false);
        stats.forEach(stat -> {
            String numberString = stat.getUri().substring(stat.getUri().lastIndexOf("/") + 1);
            Long number = Long.parseLong(numberString);
            Long eventId = number;
            views.put(eventId, views.getOrDefault(eventId, 0L) + stat.getHits());
        });

        return views;
    }

    @Override
    public Map<Long, Long> getConfirmedRequests(List<Event> events) {
        List<Long> eventsId = getPublished(events).stream()
                .map(Event::getId)
                .collect(Collectors.toList());

        Map<Long, Long> requestStats = new HashMap<>();

        if (!eventsId.isEmpty()) {
            requestRepository.getConfirmedRequests(eventsId)
                    .forEach(stat -> requestStats.put(stat.getEventId(), stat.getConfirmedRequests()));
        }

        return requestStats;
    }

    private List<Event> getPublished(List<Event> events) {
        return events.stream()
                .filter(event -> event.getPublishedOn() != null)
                .collect(Collectors.toList());
    }
}