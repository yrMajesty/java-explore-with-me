package ru.practicum.mainservice.entity;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EstimationId implements Serializable {
    private Long userId;

    private Long eventId;
}