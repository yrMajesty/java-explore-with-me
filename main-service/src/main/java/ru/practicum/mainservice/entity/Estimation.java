package ru.practicum.mainservice.entity;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@IdClass(EstimationId.class)
@Table(name = "estimations")
public class Estimation {
    @Id
    private Long userId;

    @Id
    private Long eventId;

    @Column(name = "mark", nullable = false)
    private Byte mark;
}
