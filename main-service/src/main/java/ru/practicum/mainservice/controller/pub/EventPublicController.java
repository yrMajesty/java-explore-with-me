package ru.practicum.mainservice.controller.pub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainservice.dto.event.EventFullDto;
import ru.practicum.mainservice.dto.event.EventSearchDto;
import ru.practicum.mainservice.dto.event.EventShortDto;
import ru.practicum.mainservice.service.EventPublicService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventPublicController {
    private final EventPublicService eventService;

    @GetMapping
    public List<EventShortDto> getEvents(@ModelAttribute EventSearchDto paramSearch,
                                         HttpServletRequest request) {
        log.info("Request to receive all events with request parameters: paramSearch={}, request={}",
                paramSearch, request.getRequestURI());

        return eventService.getAllEvents(paramSearch, request.getRemoteAddr(), request.getRequestURI());
    }

    @GetMapping("/{eventId}")
    public EventFullDto getEvent(@PathVariable(name = "eventId") @Positive Long eventId,
                                 HttpServletRequest request) {
        log.info("Request to get compilation with id='{}'", eventId);
        return eventService.getEventById(eventId, request.getRemoteAddr(), request.getRequestURI());
    }
}
