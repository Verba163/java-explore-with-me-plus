package ru.practicum.ewm.compilation.mapper;

import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.model.Compilation;
import ru.practicum.ewm.events.mapper.EventMapper;

import java.util.stream.Collectors;

public class CompilationMapper {

    public static CompilationDto toCompilationDto(Compilation compilation) {

        return CompilationDto.builder()
                .id(compilation.getId())
                .title(compilation.getTitle())
                .pinned(compilation.isPinned())
                .events(compilation.getEvents() != null ?
                        compilation.getEvents().stream()
                                .map(EventMapper::toEventShortDto)
                                .collect(Collectors.toList()) : null)
                .build();
    }

    public static Compilation toCompilation(CompilationDto compilationDto) {

        return Compilation.builder()
                .id(compilationDto.getId())
                .title(compilationDto.getTitle())
                .pinned(compilationDto.isPinned())
                .events(compilationDto.getEvents() != null ?
                        compilationDto.getEvents().stream()
                                .map(EventMapper::toEventEntity)
                                .collect(Collectors.toList()) : null)
                .build();
    }

    public static Compilation toCompilationEntity(NewCompilationDto newCompilationDto) {

        return Compilation.builder()
                .title(newCompilationDto.getTitle())
                .pinned(newCompilationDto.isPinned())
                .events(newCompilationDto.getEvents() != null ?
                        newCompilationDto.getEvents().stream()
                                .map(EventMapper::toEventEntity)
                                .collect(Collectors.toList()) : null)
                .build();
    }
}