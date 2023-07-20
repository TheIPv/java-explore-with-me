package ru.yandex.practicum.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.category.mapper.CategoryMapper;
import ru.yandex.practicum.category.model.Category;
import ru.yandex.practicum.category.service.CategoryPublicService;
import ru.yandex.practicum.event.dto.*;
import ru.yandex.practicum.event.enums.EventState;
import ru.yandex.practicum.event.mapper.EventMapper;
import ru.yandex.practicum.event.mapper.LocationMapper;
import ru.yandex.practicum.event.model.Event;
import ru.yandex.practicum.event.model.Location;
import ru.yandex.practicum.event.repository.EventRepository;


import org.springframework.data.domain.Pageable;
import ru.yandex.practicum.event.repository.LocationRepository;
import ru.yandex.practicum.exception.ForbiddenException;
import ru.yandex.practicum.exception.NotValidException;
import ru.yandex.practicum.user.model.User;
import ru.yandex.practicum.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventPrivateServiceImpl implements EventPrivateService {

    private final EventRepository eventRepository;
    private final LocationRepository locationRepository;
    private final UserService userService;
    private final CategoryPublicService categoryPublicService;

    @Override
    public List<EventShortDto> getUserEvents(Long userId, Pageable pageable) {
        return eventRepository.findAllByInitiatorId(userId, pageable)
                .stream()
                .map(s -> EventMapper.toShortDto(s, 0L, 0L))
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto create(Long userId, NewEventDto newEventDto) {

        checkNewEventDate(newEventDto.getEventDate(), LocalDateTime.now().plusHours(2));

        User eventUser = userService.get(userId);
        Category category = CategoryMapper.toCategoryFromDto(categoryPublicService
                .getById(newEventDto.getCategory()));
        Location eventLocation = getOrSaveLocation(newEventDto.getLocation());

        Event event = EventMapper.toModel(newEventDto, eventUser,
                category, eventLocation, LocalDateTime.now(), EventState.PENDING);

        return EventMapper.toEventFullDto(eventRepository.save(event), 0L, 0L);
    }

    @Override
    public EventFullDto getEventByCreator(Long userId, Long eventId) {
        return EventMapper.toEventFullDto(eventRepository
                        .findEventByIdAndInitiatorId(eventId, userId), 0L, 0L);
    }

    @Override
    public EventFullDto patchEventByCreator(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest) {

        checkNewEventDate(updateEventUserRequest.getEventDate(), LocalDateTime.now().plusHours(2));

        userService.get(userId);

        Event event = eventRepository.findEventByIdAndInitiatorId(eventId, userId);

        if (event.getState().equals(EventState.PUBLISHED)) {
            throw new ForbiddenException("Изменять можно только неопубликованные или отмененные события.");
        }

        if (updateEventUserRequest.getAnnotation() != null) {
            event.setAnnotation(updateEventUserRequest.getAnnotation());
        }

        if (updateEventUserRequest.getCategory() != null) {
            event.setCategory(CategoryMapper.toCategoryFromDto(categoryPublicService
                    .getById(updateEventUserRequest.getCategory())));
        }

        if (updateEventUserRequest.getDescription() != null) {
            event.setDescription(updateEventUserRequest.getDescription());
        }

        if (updateEventUserRequest.getEventDate() != null) {
            event.setEventDate(updateEventUserRequest.getEventDate());
        }

        if (updateEventUserRequest.getLocation() != null) {
            event.setLocation(getOrSaveLocation(updateEventUserRequest.getLocation()));
        }

        if (updateEventUserRequest.getPaid() != null) {
            event.setPaid(updateEventUserRequest.getPaid());
        }

        if (updateEventUserRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventUserRequest.getParticipantLimit());
        }

        if (updateEventUserRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateEventUserRequest.getRequestModeration());
        }

        if (updateEventUserRequest.getStateAction() != null) {
            switch (updateEventUserRequest.getStateAction()) {
                case SEND_TO_REVIEW:
                    event.setState(EventState.PENDING);
                    break;
                case CANCEL_REVIEW:
                    event.setState(EventState.CANCELED);
                    break;
            }
        }

        if (updateEventUserRequest.getTitle() != null) {
            event.setTitle(updateEventUserRequest.getTitle());
        }

        return EventMapper.toEventFullDto(eventRepository.save(event), 0L, 0L);
    }

    private Location getOrSaveLocation(LocationDto locationDto) {
        Location newLocation = LocationMapper.toModel(locationDto);
        return locationRepository.findByLatAndLon(newLocation.getLat(), newLocation.getLon())
                .orElseGet(() -> locationRepository.save(newLocation));
    }

    private void checkNewEventDate(LocalDateTime newEventDate, LocalDateTime minTimeBeforeEventStart) {
        if (newEventDate != null && newEventDate.isBefore(minTimeBeforeEventStart)) {
            throw new NotValidException(String.format("Field: eventDate. Error: остается слишком мало времени для " +
                    "подготовки. Value: %s", newEventDate));
        }
    }
}
