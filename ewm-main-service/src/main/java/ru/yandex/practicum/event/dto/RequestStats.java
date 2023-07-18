package ru.yandex.practicum.event.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Builder
public class RequestStats {
    Long eventId;
    Long confirmedRequests;
    public RequestStats(Long eventId, Long confirmedRequests) {
        this.eventId = eventId;
        this.confirmedRequests = confirmedRequests;
    }
}