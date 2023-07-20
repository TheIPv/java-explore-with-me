package ru.yandex.practicum.compilation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.compilation.dto.CompilationDto;
import ru.yandex.practicum.compilation.dto.NewCompilationDto;
import ru.yandex.practicum.compilation.dto.UpdateCompilationRequest;
import ru.yandex.practicum.compilation.mapper.CompilationMapper;
import ru.yandex.practicum.compilation.model.Compilation;
import ru.yandex.practicum.compilation.repository.CompilationRepository;
import ru.yandex.practicum.event.dto.EventShortDto;
import ru.yandex.practicum.event.model.Event;
import ru.yandex.practicum.event.service.EventPublicService;
import ru.yandex.practicum.exception.NotFoundException;

import java.util.*;

@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {

    private final EventPublicService eventService;
    private final CompilationRepository compilationRepository;

    @Override
    public CompilationDto create(NewCompilationDto newCompilationDto) {
        List<Event> events = new ArrayList<>();

        if (newCompilationDto.getEvents() != null) {
            events = eventService.getEventsByIds(newCompilationDto.getEvents());
            checkSize(events, newCompilationDto.getEvents());
        }

        Compilation compilation = compilationRepository.save(CompilationMapper
                .newDtoToCompilation(newCompilationDto, events));

        return getById(compilation.getId());
    }

    @Override
    public CompilationDto patch(Long compId, UpdateCompilationRequest updateCompilationRequest) {
        Compilation compilation = getCompilationById(compId);

        if (updateCompilationRequest.getTitle() != null) {
            compilation.setTitle(updateCompilationRequest.getTitle());
        }

        if (updateCompilationRequest.getPinned() != null) {
            compilation.setPinned(updateCompilationRequest.getPinned());
        }

        if (updateCompilationRequest.getEvents() != null) {
            List<Event> events = eventService.getEventsByIds(updateCompilationRequest.getEvents());

            checkSize(events, updateCompilationRequest.getEvents());

            compilation.setEvents(events);
        }

        compilationRepository.save(compilation);

        return getById(compId);
    }

    @Override
    public void deleteById(Long compId) {
        existById(compId);
        compilationRepository.deleteById(compId);
    }

    @Override
    public List<CompilationDto> getAll(Boolean pinned, Pageable pageable) {
        List<Compilation> compilations = compilationRepository
                .findAllByFilter(pinned, pageable);

        Set<Event> uniqueEvents = new HashSet<>();
        compilations.forEach(compilation -> uniqueEvents.addAll(compilation.getEvents()));

        Map<Long, EventShortDto> eventsShortDto = new HashMap<>();
        eventService.toEventsShortDto(new ArrayList<>(uniqueEvents))
                .forEach(event -> eventsShortDto.put(event.getId(), event));

        List<CompilationDto> result = new ArrayList<>();
        compilations.forEach(compilation -> {
            List<EventShortDto> compEventsShortDto = new ArrayList<>();
            compilation.getEvents()
                    .forEach(event -> compEventsShortDto.add(eventsShortDto.get(event.getId())));
            result.add(CompilationMapper.toCompilationDto(compilation, compEventsShortDto));
        });

        return result;
    }

    @Override
    public CompilationDto getById(Long compId) {

        Compilation compilation = getCompilationById(compId);

        List<EventShortDto> eventsShortDto = eventService.toEventsShortDto(compilation.getEvents());

        return CompilationMapper.toCompilationDto(compilation, eventsShortDto);
    }

    private void checkSize(List<Event> events, List<Long> eventsIdToUpdate) {
        if (events.size() != eventsIdToUpdate.size()) {
            throw new NotFoundException("Some events wasn't found");
        }
    }

    private Compilation getCompilationById(Long id) {
        return compilationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Compilation with this id doesn't exist"));
    }

    private void existById(Long id) {
        if (!compilationRepository.existsById(id)) {
            throw new NotFoundException("Compilation with this id doesn't exist");
        }
    }
}
