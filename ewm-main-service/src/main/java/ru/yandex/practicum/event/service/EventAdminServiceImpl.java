package ru.yandex.practicum.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.category.mapper.CategoryMapper;
import ru.yandex.practicum.category.service.CategoryPublicService;
import ru.yandex.practicum.event.dto.EventFullDto;
import ru.yandex.practicum.event.dto.LocationDto;
import ru.yandex.practicum.event.dto.UpdateEventAdminRequest;
import ru.yandex.practicum.event.enums.EventState;
import ru.yandex.practicum.event.mapper.EventMapper;
import ru.yandex.practicum.event.mapper.LocationMapper;
import ru.yandex.practicum.event.model.Event;
import ru.yandex.practicum.event.model.Location;
import ru.yandex.practicum.event.repository.EventRepository;
import ru.yandex.practicum.event.repository.LocationRepository;
import ru.yandex.practicum.exception.ForbiddenException;
import ru.yandex.practicum.exception.NotValidException;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventAdminServiceImpl implements EventAdminService {
    private final EventRepository eventRepository;
    private final LocationRepository locationRepository;
    private final CategoryPublicService categoryService;
    private final MainStatsService mainStatsService;

    @Override
    public List<EventFullDto> getEvents(List<Long> users, List<EventState> states, List<Long> categories,
                                        LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size) {
        if (rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
            throw new NotValidException("Error: rageStart date is after end rageEnd");
        }

        List<Event> events;

        if (rangeStart == null || rangeEnd == null) {
            events = eventRepository.getEventsByAdminWithoutDate(users, states, categories,
                    PageRequest.of(from / size, size));
        } else {
            events = eventRepository.getEventsByAdmin(users, states, categories, rangeStart,
                    rangeEnd, PageRequest.of(from / size, size));
        }
        Map<Long, Long> requestStats = mainStatsService.getConfirmedRequests(events);

        Map<Long, Long> views = mainStatsService.getViews(events);


        return  events
                .stream()
                .map(s -> EventMapper.toEventFullDto(
                        s, requestStats.get(s.getId()) == null ? 0L: requestStats.get(s.getId())
                        , views.get(s.getId())))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventFullDto updateEvent(Long eventId, UpdateEventAdminRequest updateEventAdminRequest) {
        if (updateEventAdminRequest.getEventDate() != null && updateEventAdminRequest
                .getEventDate().isBefore(LocalDateTime.now())) {
            throw new NotValidException("Error: Event date can't be earlier than now");
        }

        checkNewEventDate(updateEventAdminRequest.getEventDate(), LocalDateTime.now().plusHours(1));

        Event event = eventRepository.findById(eventId).get();

        if (updateEventAdminRequest.getAnnotation() != null) {
            event.setAnnotation(updateEventAdminRequest.getAnnotation());
        }

        if (updateEventAdminRequest.getDescription() != null) {
            event.setDescription(updateEventAdminRequest.getDescription());
        }

        if (updateEventAdminRequest.getCategory() != null) {
           event.setCategory(CategoryMapper.toCategoryFromDto(categoryService
                   .getById(updateEventAdminRequest.getCategory())));
        }

        if (updateEventAdminRequest.getEventDate() != null) {
            event.setEventDate(updateEventAdminRequest.getEventDate());
        }

        if (updateEventAdminRequest.getPaid() != null) {
            event.setPaid(updateEventAdminRequest.getPaid());
        }

        if (updateEventAdminRequest.getLocation() != null) {
            event.setLocation(getOrSaveLocation(updateEventAdminRequest.getLocation()));
        }

        if (updateEventAdminRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventAdminRequest.getParticipantLimit());
        }

        if (updateEventAdminRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateEventAdminRequest.getRequestModeration());
        }

        if (updateEventAdminRequest.getStateAction() != null) {
            if (!event.getState().equals(EventState.PENDING)) {
                throw new ForbiddenException("Validation Error");
            }

            switch (updateEventAdminRequest.getStateAction()) {
                case PUBLISH_EVENT:
                    event.setState(EventState.PUBLISHED);
                    event.setPublishedOn(LocalDateTime.now());
                    break;
                case REJECT_EVENT:
                    event.setState(EventState.REJECTED);
                    break;
            }
        }

        if (updateEventAdminRequest.getTitle() != null) {
            event.setTitle(updateEventAdminRequest.getTitle());
        }

        return EventMapper.toEventFullDto(eventRepository.save(event), 0L, 0L);
    }

    private void checkNewEventDate(LocalDateTime newEventDate, LocalDateTime minTimeBeforeEventStart) {
        if (newEventDate != null && newEventDate.isBefore(minTimeBeforeEventStart)) {
            throw new ForbiddenException(String.format("Field: eventDate. Error: остается слишком мало времени для " +
                    "подготовки. Value: %s", newEventDate));
        }
    }

    private Location getOrSaveLocation(LocationDto locationDto) {
        Location newLocation = LocationMapper.toModel(locationDto);
        return locationRepository.findByLatAndLon(newLocation.getLat(), newLocation.getLon())
                .orElseGet(() -> locationRepository.save(newLocation));
    }

}
