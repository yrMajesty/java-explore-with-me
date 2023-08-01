package ru.practicum.mainservice.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.mainservice.dto.request.RequestDto;
import ru.practicum.mainservice.entity.Request;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RequestMapper {
    @Mapping(target = "event", source = "event.id")
    @Mapping(target = "requester", source = "requester.id")
    RequestDto toDto(Request request);

    List<RequestDto> toDtos(List<Request> requests);
}
