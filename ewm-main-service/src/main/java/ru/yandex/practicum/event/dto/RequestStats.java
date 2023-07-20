package ru.yandex.practicum.event.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
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