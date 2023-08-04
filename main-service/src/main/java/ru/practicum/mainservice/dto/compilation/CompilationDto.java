package ru.practicum.mainservice.dto.compilation;

import lombok.*;
import ru.practicum.mainservice.dto.event.EventShortDto;

import java.util.Set;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CompilationDto {
    private Set<EventShortDto> events;

    private Long id;

    private Boolean pinned;

    private String title;
}