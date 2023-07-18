package ru.yandex.practicum.event.mapper;

import ru.yandex.practicum.event.dto.ParticipationRequestDto;
import ru.yandex.practicum.event.model.Request;

public class RequestMapper {
    public static ParticipationRequestDto toParticipationRequestDto(Request request) {
        return ParticipationRequestDto.builder()
                .id(request.getId())
                .event(request.getEvent().getId())
                .requester(request.getRequester().getId())
                .created(request.getCreated())
                .status(request.getStatus().name())
                .build();
    }
}
