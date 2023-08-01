package ru.practicum.mainservice.exception;

import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {
    private String message;

    private String reason;

    private String status;

    private LocalDateTime timestamp;
}
