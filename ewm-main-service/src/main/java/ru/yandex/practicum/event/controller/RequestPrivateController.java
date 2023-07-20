package ru.yandex.practicum.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.event.dto.ParticipationRequestDto;
import ru.yandex.practicum.event.service.RequestPrivateService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/requests")
@Slf4j
public class RequestPrivateController {
    private final RequestPrivateService requestService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequestDto> getEventRequestsByRequester(@PathVariable Long userId) {
        log.info("Received GET /users/{userId}/requests");
        return requestService.getEventRequestsByUser(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto createEventRequest(@PathVariable Long userId,
                                                      @RequestParam Long eventId) {
        log.info("Received POST /users/{userId}/requests");
        return requestService.add(userId, eventId);
    }

    @PatchMapping("/{requestId}/cancel")
    @ResponseStatus(HttpStatus.OK)
    public ParticipationRequestDto cancelEventRequest(@PathVariable Long userId,
                                                      @PathVariable Long requestId) {
        log.info("Received PATCH /users/{userId}/requests/{requestId}/cancel");
        return requestService.cancel(userId, requestId);
    }
}