package ru.practicum.mainservice.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainservice.dto.event.EventFullDto;
import ru.practicum.mainservice.dto.event.EventSearchDto;
import ru.practicum.mainservice.dto.event.EventUpdateDto;
import ru.practicum.mainservice.service.EventAdminService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/admin/events")
@RequiredArgsConstructor
public class EventAdminController {
    private final EventAdminService eventService;

    @GetMapping
    public List<EventFullDto> getEvents(@ModelAttribute EventSearchDto request) {
        log.info("Request to get all events with parameters: {}", request);
        return eventService.getAllEvents(request);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEvent(@PathVariable(name = "eventId") Long eventId,
                                    @RequestBody @Valid EventUpdateDto request) {
        log.info("Event update request with id='{}', new parameters={}", eventId, request);
        return eventService.updateEventById(eventId, request);
    }

}
