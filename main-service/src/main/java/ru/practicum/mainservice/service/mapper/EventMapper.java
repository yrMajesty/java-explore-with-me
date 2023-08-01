package ru.practicum.mainservice.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import ru.practicum.mainservice.dto.event.EventFullDto;
import ru.practicum.mainservice.dto.event.EventNewDto;
import ru.practicum.mainservice.dto.event.EventShortDto;
import ru.practicum.mainservice.entity.Category;
import ru.practicum.mainservice.entity.Event;
import ru.practicum.mainservice.entity.User;

import java.util.List;

@Mapper(componentModel = "spring", uses = {CategoryMapper.class, LocationMapper.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface
EventMapper {
    @Mapping(target = "category", source = "category")
    @Mapping(target = "initiator", source = "initiator")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "publishedOn", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "createdOn", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "state", expression = "java(ru.practicum.mainservice.model.EventState.PENDING)")
    @Mapping(target = "confirmedRequests", constant = "0")
    Event fromDto(EventNewDto eventNewDto, Category category, User initiator);

    EventShortDto toShortDto(Event event);

    EventFullDto toFullDto(Event event);

    List<EventShortDto> toShortDtos(List<Event> events);

    List<EventFullDto> toFullDtos(List<Event> events);
}