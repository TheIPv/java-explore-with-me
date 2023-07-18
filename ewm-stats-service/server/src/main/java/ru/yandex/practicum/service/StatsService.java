package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.EndpointHitDto;
import ru.yandex.practicum.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {
    EndpointHitDto save(EndpointHitDto endpointHitDto);

    List<ViewStatsDto> get(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);
}
