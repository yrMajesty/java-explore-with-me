package ru.practicum.mainservice.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import ru.practicum.mainservice.dto.compilation.CompilationDto;
import ru.practicum.mainservice.dto.compilation.CompilationNewDto;
import ru.practicum.mainservice.entity.Compilation;
import ru.practicum.mainservice.entity.Event;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CompilationMapper {
    CompilationDto toDto(Compilation compilation);

    Compilation fromDto(CompilationNewDto dto);

    List<CompilationDto> toDtos(List<Compilation> compilations);

    default List<Event> map(List<Long> ids) {
        if (Objects.nonNull(ids)) {
            return ids.stream()
                    .map(id -> Event.builder().id(id).build())
                    .collect(Collectors.toList());
        }
        return List.of();
    }
}
