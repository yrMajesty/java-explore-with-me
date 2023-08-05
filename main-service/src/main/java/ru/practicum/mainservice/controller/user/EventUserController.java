package ru.practicum.mainservice.controller.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainservice.dto.event.EventFullDto;
import ru.practicum.mainservice.dto.event.EventNewDto;
import ru.practicum.mainservice.dto.event.EventShortDto;
import ru.practicum.mainservice.dto.event.EventUpdateDto;
import ru.practicum.mainservice.dto.event.enums.EventSortType;
import ru.practicum.mainservice.dto.request.RequestDto;
import ru.practicum.mainservice.dto.request.RequestStatusUpdateRequest;
import ru.practicum.mainservice.dto.request.RequestStatusUpdateResponse;
import ru.practicum.mainservice.service.EstimationService;
import ru.practicum.mainservice.service.EventUserService;
import ru.practicum.mainservice.service.RequestService;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class EventUserController {
    private final EventUserService eventService;
    private final RequestService requestService;
    private final EstimationService estimationService;

    @PostMapping("/{userId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createEvent(@PathVariable(name = "userId") @Positive Long userId,
                                    @RequestBody @Valid EventNewDto request) {
        log.info("Request to create event {} from user with id='{}'", request, userId);
        return eventService.createEvent(request, userId);
    }

    @GetMapping("/{userId}/events")
    public List<EventShortDto> getEvents(@PathVariable(name = "userId") @Positive Long userId,
                                         @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                         @RequestParam(name = "size", defaultValue = "10") @Positive Integer size,
                                         @RequestParam(name = "sortBy", defaultValue = "ID") EventSortType sortBy,
                                         @RequestParam(name = "direction", defaultValue = "ASC") Sort.Direction direction) {
        log.info("Request to get events by userId from user with id='{}'", userId);
        return eventService.getEventsByUserId(userId, from, size, sortBy, direction);
    }

    @GetMapping("/{userId}/events/{eventId}")
    public EventFullDto getEventByUserAndEvent(@PathVariable(name = "userId") @Positive Long userId,
                                               @PathVariable(name = "eventId") @Positive Long eventId) {
        log.info("Request to get event by userId='{}' and eventId='{}' from user with id='{}'", userId, eventId, userId);
        return eventService.getEventByUserIdAndEventId(userId, eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}")
    public EventFullDto updateEventByOwner(@PathVariable(name = "userId") @Positive Long userId,
                                           @PathVariable(name = "eventId") @Positive Long eventId,
                                           @RequestBody @Valid EventUpdateDto request) {
        log.info(" Request to update event  with id='{}' from user with id='{}', request={}",
                eventId, userId, request);
        return eventService.updateEventByIdAndUserId(eventId, userId, request);
    }

    @GetMapping("/{userId}/events/{eventId}/requests")
    public List<RequestDto> getRequestByEvent(@PathVariable(name = "userId") Long userId,
                                              @PathVariable(name = "eventId") Long eventId) {
        log.info("Request to get requests by eventId='{}' from user with id='{}'", eventId, userId);
        return requestService.getRequestsByEventId(userId, eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}/requests")
    public RequestStatusUpdateResponse updateStatusRequest(@PathVariable(name = "userId") @Positive Long userId,
                                                           @PathVariable(name = "eventId") @Positive Long eventId,
                                                           @RequestBody RequestStatusUpdateRequest request) {
        log.info("Request to update status event with id='{}' from user with id='{}'", eventId, userId);
        return eventService.updateStatusRequestByEventId(userId, eventId, request);
    }

    @PostMapping("/{userId}/events/{eventId}/rating")
    public ResponseEntity<Object> rateEvent(@PathVariable(name = "userId") @Positive Long userId,
                                            @PathVariable(name = "eventId") @Positive Long eventId,
                                            @RequestParam(name = "mark") @Min(0) @Max(10) Byte mark) {
        log.info("Request to add mark {} for event with id='{}' by user with id='{}'", mark, eventId, userId);
        estimationService.addEventMark(userId, eventId, mark);
        return new ResponseEntity<>(
                Map.of("message",
                        String.format("User with id='%s' rated event with id='%s' a rating of %s", userId, eventId, mark)),
                HttpStatus.OK);
    }

    @DeleteMapping("/{userId}/events/{eventId}/rating")
    public ResponseEntity<Object> deleteEventRating(@PathVariable(name = "userId") @Positive Long userId,
                                                    @PathVariable(name = "eventId") @Positive Long eventId) {
        log.info("Delete mark for event with id='{}' by user with id='{}'", eventId, userId);
        estimationService.deleteEventMark(userId, eventId);
        return new ResponseEntity<>(
                Map.of("message",
                        String.format("User with id='%s' deleted for event with id='%s'", userId, eventId)),
                HttpStatus.OK);
    }
}