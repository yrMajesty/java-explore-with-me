package ru.practicum.mainservice.dto.request;

import lombok.*;

import java.util.List;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RequestStatusUpdateResponse {
    private List<RequestDto> confirmedRequests;

    private List<RequestDto> rejectedRequests;
}
