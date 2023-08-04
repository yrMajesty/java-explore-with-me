package ru.practicum.statsserver.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "stats")
public class ItemStats {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "app", nullable = false)
    private String app;

    @Column(name = "uri", nullable = false)
    private String uri;

    @Column(name = "ip", nullable = false, length = 32)
    private String ip;

    @Column(name = "request_date_time", nullable = false)
    private LocalDateTime requestDateTime;

}