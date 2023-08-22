package ru.practicum.mainservice.dto.db;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EventMarkRequestResult {
    private Long eventId;
    private Double mark;
}
