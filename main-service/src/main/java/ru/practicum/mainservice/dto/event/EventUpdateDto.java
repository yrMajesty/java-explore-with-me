package ru.practicum.mainservice.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.mainservice.dto.location.LocationDto;
import ru.practicum.mainservice.entity.enums.StateAction;
import ru.practicum.mainservice.utils.DateTimeUtils;

import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventUpdateDto {
    @Size(min = 20, max = 2000, message = "Annotation must be 20-2000 characters")
    private String annotation;

    private Long category;

    @Size(min = 20, max = 7000, message = "Description must be 20-7000 characters")
    private String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateTimeUtils.DATE_TIME_FORMAT)
    private LocalDateTime eventDate;

    private LocationDto location;

    private Boolean paid;

    private Integer participantLimit;

    private Boolean requestModeration;

    private StateAction stateAction;

    @Size(min = 3, max = 120, message = "Title must be 3-120 characters")
    private String title;
}
