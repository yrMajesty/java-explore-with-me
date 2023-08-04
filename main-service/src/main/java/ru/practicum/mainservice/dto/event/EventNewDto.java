package ru.practicum.mainservice.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.mainservice.dto.location.LocationDto;
import ru.practicum.mainservice.utils.DateTimeUtils;

import javax.validation.constraints.*;
import java.time.LocalDateTime;

@Setter
@Getter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class EventNewDto {
    @Size(min = 20, max = 2000, message = "Annotation must be 20-2000 characters")
    @NotBlank(message = "Annotation cannot be empty or null")
    private String annotation;

    @NotNull(message = "Category cannot be null")
    @Positive(message = "Category id must be positive number")
    private Long category;

    @Size(min = 20, max = 7000, message = "Description must be 20-7000 characters")
    @NotBlank(message = "Description can not be empty or null")
    private String description;

    @NotNull(message = "Event date cannot be null")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateTimeUtils.DATE_TIME_FORMAT)
    private LocalDateTime eventDate;

    @NotNull(message = "Location cannot be null")
    private LocationDto location;

    @Builder.Default
    private Boolean paid = false;

    @Builder.Default
    @PositiveOrZero
    private Integer participantLimit = 0;

    @Builder.Default
    private Boolean requestModeration = true;

    @Size(min = 3, max = 120, message = "Title must be 3-120 characters")
    @NotBlank(message = "Title cannot be empty or null")
    private String title;

}
