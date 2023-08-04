package ru.practicum.mainservice.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import ru.practicum.mainservice.dto.location.LocationDto;
import ru.practicum.mainservice.entity.Location;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LocationMapper {
    Location fromDto(LocationDto dto);

    LocationDto toDto(Location location);
}
