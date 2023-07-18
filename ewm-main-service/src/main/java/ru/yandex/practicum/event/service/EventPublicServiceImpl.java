package ru.yandex.practicum.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.event.dto.EventFullDto;
import ru.yandex.practicum.event.dto.EventShortDto;
import ru.yandex.practicum.event.enums.EventSortType;
import ru.yandex.practicum.event.enums.EventState;
import ru.yandex.practicum.event.mapper.EventMapper;
import ru.yandex.practicum.event.model.Event;
import ru.yandex.practicum.event.repository.EventRepository;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.exception.NotValidException;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class EventPublicServiceImpl implements EventPublicService {

    private final EventRepository eventRepository;
    private final MainStatsService mainStatsService;

    @Override
    public List<EventShortDto> getEvents(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                                         LocalDateTime rangeEnd, Boolean onlyAvailable, EventSortType sort,
                                         Integer from, Integer size, HttpServletRequest request) {

        if(rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
            throw new NotValidException("Error: rangeStart date is after end rangeEnd");
        }

        List<Event> events = eventRepository.getEventsByPublic(text, categories, paid, rangeStart,
                rangeEnd, PageRequest.of(from / size, size));

        if (events.isEmpty()) {
            return List.of();
        }

        mainStatsService.addHit(request);

        Map<Long, Long> requestStats = mainStatsService.getConfirmedRequests(events);

        Map<Long, Long> views = mainStatsService.getViews(events);


        return events
                .stream()
                .map(s -> EventMapper.toShortDto(s, requestStats.get(s.getId()),
                        views.get(s.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto getEvent(Long id, HttpServletRequest request) {

        Event event = getEventById(id);

        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new NotFoundException("Событие с таким id не опубликовано.");
        }

        mainStatsService.addHit(request);

        eventRepository.flush();

        Long views = mainStatsService.getViews(List.of(event)).get(event.getId());
        if(views == null) {
            views = Long.valueOf(1);
        }

        Long confirmedRequests = mainStatsService.getConfirmedRequests(List.of(event)).get(event.getId());

        return EventMapper.toEventFullDto(event, confirmedRequests, views);
    }

    @Override
    public Event getEventById(Long eventId) {

        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("События с таким id не существует."));
    }

    @Override
    public List<Event> getEventsByIds(List<Long> eventsId) {

        if (eventsId.isEmpty()) {
            return List.of();
        }

        return eventRepository.findAllByIdIn(eventsId);
    }

    @Override
    public List<EventShortDto> toEventsShortDto(List<Event> events) {
        Map<Long, Long> views = mainStatsService.getViews(events);
        Map<Long, Long> confirmedRequests = mainStatsService.getConfirmedRequests(events);

        return events.stream()
                .map((event) -> EventMapper.toShortDto(
                        event,
                        confirmedRequests.getOrDefault(event.getId(), 0L),
                        views.getOrDefault(event.getId(), 0L)))
                .collect(Collectors.toList());
    }
}
