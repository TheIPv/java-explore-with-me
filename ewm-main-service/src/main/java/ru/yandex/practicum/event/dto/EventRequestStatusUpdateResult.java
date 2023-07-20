package ru.yandex.practicum.event.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.event.dto.ParticipationRequestDto;

import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventRequestStatusUpdateResult {
    List<ParticipationRequestDto> confirmedRequests;
    List<ParticipationRequestDto> rejectedRequests;
}
