

package ru.yandex.practicum.event.service;

import ru.yandex.practicum.event.dto.EventRequestStatusUpdateRequest;
import ru.yandex.practicum.event.dto.EventRequestStatusUpdateResult;
import ru.yandex.practicum.event.dto.ParticipationRequestDto;

import java.util.List;

public interface RequestPrivateService {
    List<ParticipationRequestDto> getEventRequestsByUser(Long userId);
    ParticipationRequestDto add(Long userId, Long eventId);
    ParticipationRequestDto cancel(Long userId, Long requestId);
    List<ParticipationRequestDto> getEventRequestsByOwner(Long ownerId, Long eventId);
    EventRequestStatusUpdateResult patchEventRequestByOwner(Long ownerId, Long eventId,
                                                            EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest);
}
