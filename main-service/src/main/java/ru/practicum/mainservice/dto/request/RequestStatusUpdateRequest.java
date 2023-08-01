package ru.practicum.mainservice.dto.request;

import lombok.*;
import ru.practicum.mainservice.model.RequestStatus;

import java.util.Set;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RequestStatusUpdateRequest {
    private Set<Long> requestIds;

    private RequestStatus status;
}
