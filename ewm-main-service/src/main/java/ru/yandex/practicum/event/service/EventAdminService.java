package ru.yandex.practicum.event.service;

import ru.yandex.practicum.event.dto.EventFullDto;
import ru.yandex.practicum.event.dto.UpdateEventAdminRequest;
import ru.yandex.practicum.event.enums.EventState;

import java.time.LocalDateTime;
import java.util.List;

public interface EventAdminService {

    List<EventFullDto> getEvents(List<Long> users, List<EventState> states, List<Long> categories,
                                 LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size);

    EventFullDto updateEvent(Long eventId, UpdateEventAdminRequest updateEventAdminRequest);
}
