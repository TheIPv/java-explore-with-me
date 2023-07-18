package ru.yandex.practicum.event.service;

import ru.yandex.practicum.event.dto.EventFullDto;
import ru.yandex.practicum.event.dto.EventShortDto;
import ru.yandex.practicum.event.dto.NewEventDto;
import ru.yandex.practicum.event.dto.UpdateEventUserRequest;

import org.springframework.data.domain.Pageable;
import java.util.List;

public interface EventPrivateService {
    List<EventShortDto> getUserEvents(Long userId, Pageable pageable);
    EventFullDto create(Long userId, NewEventDto newEventDto);
    EventFullDto getEventByCreator(Long userId, Long eventId);
    EventFullDto patchEventByCreator(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest);

}
