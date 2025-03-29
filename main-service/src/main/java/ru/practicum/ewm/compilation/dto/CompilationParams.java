package ru.practicum.ewm.compilation.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CompilationParams {

    private Boolean pinned;
    private Long from;
    private Long size;
    private Long compId;
    private NewCompilationDto newCompilationDto;
}
