package ru.practicum.mainservice.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.mainservice.model.RequestStatus;
import ru.practicum.mainservice.utils.DateTimeUtils;

import java.time.LocalDateTime;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestDto {
    private Long id;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateTimeUtils.DATE_TIME_FORMAT)
    private LocalDateTime created;

    private Long event;

    private Long requester;

    private RequestStatus status;
}
