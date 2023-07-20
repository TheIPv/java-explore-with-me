package ru.yandex.practicum.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.event.dto.*;
import ru.yandex.practicum.event.service.EventPrivateService;
import ru.yandex.practicum.event.service.RequestPrivateService;


import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/events")
@Validated
@Slf4j
public class EventPrivateController {
    private final EventPrivateService eventService;
    private final RequestPrivateService requestService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> getAllEventsByPrivate(
            @PathVariable Long userId,
            @RequestParam(required = false, defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(required = false, defaultValue = "10") @Positive Integer size) {
        log.info("Received GET /users/{userId}/events request");
        return eventService.getUserEvents(userId, PageRequest.of(from / size, size));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createEventByPrivate(@PathVariable Long userId,
                                             @Valid @RequestBody NewEventDto newEventDto) {
        log.info("Received POST /users/{userId}/events request");
        return eventService.create(userId, newEventDto);
    }

    @GetMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto getEventByPrivate(
            @PathVariable Long userId,
            @PathVariable Long eventId) {
        log.info("Received GET /users/{userId}/events/{eventId} request");
        return eventService.getEventByCreator(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto patchEventByPrivate(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @Valid @RequestBody UpdateEventUserRequest updateEventUserRequest) {
        log.info("Received PATCH /users/{userId}/events/{eventId} request");
        return eventService.patchEventByCreator(userId, eventId, updateEventUserRequest);
    }

    @GetMapping("/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequestDto> getEventRequestsByEventOwner(
            @PathVariable Long userId,
            @PathVariable Long eventId) {
        log.info("Received GET /users/{userId}/events/{eventId}/requests request");
        return requestService.getEventRequestsByOwner(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public EventRequestStatusUpdateResult patchEventRequestsByEventOwner(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @Valid @RequestBody EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        log.info("Received PATCH /users/{userId}/events/{eventId}/requests request");
        return requestService.patchEventRequestByOwner(userId, eventId, eventRequestStatusUpdateRequest);
    }
}