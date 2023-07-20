package ru.yandex.practicum.event.service;

import ru.yandex.practicum.event.model.Event;
import ru.yandex.practicum.dto.ViewStatsDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface MainStatsService {
    void addHit(HttpServletRequest request);

    List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);

    Map<Long, Long> getViews(List<Event> events);

    Map<Long, Long> getConfirmedRequests(List<Event> events);
}
