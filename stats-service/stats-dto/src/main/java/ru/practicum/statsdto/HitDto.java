package ru.practicum.statsdto;

import lombok.*;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HitDto {
    private String app;
    private String uri;
    private String ip;
    private String timestamp;

}
