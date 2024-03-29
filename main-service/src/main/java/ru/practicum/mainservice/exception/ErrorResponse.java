package ru.practicum.mainservice.exception;

import lombok.*;

import java.util.Map;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {
    private String message;

    private Map<String, Object> errors;

    private String reason;

    private String status;

    private String timestamp;
}
