package ru.practicum.mainservice.dto.location;

import lombok.*;

import javax.validation.constraints.NotNull;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LocationDto {
    @NotNull(message = "Latitude cannot be empty or null")
    private Float lat;

    @NotNull(message = "Longitude cannot be empty or null")
    private Float lon;
}