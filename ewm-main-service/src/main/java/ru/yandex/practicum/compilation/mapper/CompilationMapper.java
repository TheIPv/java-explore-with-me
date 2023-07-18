package ru.yandex.practicum.compilation.mapper;

import ru.yandex.practicum.compilation.dto.CompilationDto;
import ru.yandex.practicum.compilation.dto.NewCompilationDto;
import ru.yandex.practicum.compilation.model.Compilation;
import ru.yandex.practicum.event.dto.EventShortDto;
import ru.yandex.practicum.event.model.Event;

import java.util.List;

public class CompilationMapper {
    public static Compilation newDtoToCompilation(NewCompilationDto newCompilationDto, List<Event> events) {
        return Compilation
                .builder()
                .title(newCompilationDto.getTitle())
                .pinned(newCompilationDto.getPinned() == null ?
                        false : newCompilationDto.getPinned())
                .events(events)
                .build();
    }
    public static CompilationDto toCompilationDto(Compilation compilation, List<EventShortDto> eventsShortDto) {
        return CompilationDto
                .builder()
                .id(compilation.getId())
                .title(compilation.getTitle())
                .pinned(compilation.getPinned())
                .events(eventsShortDto)
                .build();
    }
}
