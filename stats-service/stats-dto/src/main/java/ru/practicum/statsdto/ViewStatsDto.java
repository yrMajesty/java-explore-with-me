package ru.practicum.statsdto;

import lombok.*;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ViewStatsDto {
    private String app;
    private String uri;
    private Long hits;
}
