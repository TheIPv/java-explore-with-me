package ru.yandex.practicum.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.dto.EndpointHitDto;
import ru.yandex.practicum.dto.ViewStatsDto;
import ru.yandex.practicum.mapper.EndpointHitMapper;
import ru.yandex.practicum.mapper.ViewStatsMapper;
import ru.yandex.practicum.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
@AllArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final StatsRepository statsRepository;

    @Override
    public EndpointHitDto save(EndpointHitDto endpointHitDto) {
        return EndpointHitMapper.toEndpointHitDto(statsRepository.
                save(EndpointHitMapper.toEndpointHit(endpointHitDto)));
    }

    @Override
    public List<ViewStatsDto> get(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        PageRequest pageable = PageRequest.of(0, 10);
        if(unique) {
            return statsRepository.getUniqueViewStats(start, end, uris, pageable)
                    .stream()
                    .map(ViewStatsMapper::toViewStatsDto)
                    .collect(toList());
        } else {
            return statsRepository.getViewStats(start, end, uris, pageable)
                    .stream()
                    .map(ViewStatsMapper::toViewStatsDto)
                    .collect(toList());
        }
    }
}
