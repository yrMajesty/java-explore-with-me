package ru.practicum.mainservice.dto.event;

import lombok.*;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.mainservice.dto.event.enums.EventSortType;
import ru.practicum.mainservice.utils.DateTimeUtils;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventSearchDto {
    private List<Long> users;

    private List<String> states;

    private List<Long> categories;

    @DateTimeFormat(pattern = DateTimeUtils.DATE_TIME_FORMAT)
    private LocalDateTime rangeStart;

    @DateTimeFormat(pattern = DateTimeUtils.DATE_TIME_FORMAT)
    private LocalDateTime rangeEnd;

    private String text;

    private Boolean paid;

    private Boolean onlyAvailable;

    @PositiveOrZero
    private Integer from = 0;

    @Positive
    private Integer size = 10;

    private EventSortType sortBy = EventSortType.ID;

    private Sort.Direction direction = Sort.Direction.ASC;
}
