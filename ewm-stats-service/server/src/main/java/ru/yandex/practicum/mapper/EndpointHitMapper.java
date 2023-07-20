package ru.yandex.practicum.mapper;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import ru.yandex.practicum.dto.EndpointHitDto;
import ru.yandex.practicum.model.EndpointHit;

@RequiredArgsConstructor
@Builder
public class EndpointHitMapper {
    public static EndpointHitDto toEndpointHitDto(EndpointHit endpointHit) {
        return EndpointHitDto.builder()
                .id(endpointHit.getId())
                .app(endpointHit.getApp())
                .ip(endpointHit.getIp())
                .uri(endpointHit.getUri())
                .timestamp(endpointHit.getTimestamp())
                .build();
    }

    public static EndpointHit toEndpointHit(EndpointHitDto endpointHitDto) {
        return EndpointHit.builder()
                .app(endpointHitDto.getApp())
                .ip(endpointHitDto.getIp())
                .uri(endpointHitDto.getUri())
                .timestamp(endpointHitDto.getTimestamp())
                .build();
    }

}
