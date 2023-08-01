package ru.practicum.mainservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainservice.dto.event.EventFullDto;
import ru.practicum.mainservice.dto.event.EventNewDto;
import ru.practicum.mainservice.dto.event.EventShortDto;
import ru.practicum.mainservice.dto.event.EventUpdateDto;
import ru.practicum.mainservice.dto.request.RequestDto;
import ru.practicum.mainservice.dto.request.RequestStatusUpdateRequest;
import ru.practicum.mainservice.dto.request.RequestStatusUpdateResponse;
import ru.practicum.mainservice.service.EventUserService;
import ru.practicum.mainservice.service.RequestService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final EventUserService eventService;
    private final RequestService requestService;

    @PostMapping("/{userId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createEvent(@PathVariable(name = "userId") @Positive Long userId,
                                    @RequestBody @Valid EventNewDto request) {
        log.info("EventUserController: Request to create event {} from user with id='{}'", request, userId);
        return eventService.createEvent(request, userId);
    }

    @GetMapping("/{userId}/events")
    public List<EventShortDto> getEvents(@PathVariable(name = "userId") @Positive Long userId,
                                         @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                         @RequestParam(name = "size", defaultValue = "10") @Positive Integer size) {
        log.info("EventUserController: Request to get events by userId from user with id='{}'", userId);
        return eventService.getEventsByUserId(userId, from, size);
    }


    @GetMapping("/{userId}/events/{eventId}")
    public EventFullDto getEventByUserAndEvent(@PathVariable(name = "userId") @Positive Long userId,
                                               @PathVariable(name = "eventId") @Positive Long eventId) {
        log.info("EventUserController: Request to get event by userId='{}' and eventId='{}' from user with id='{}'",
                userId, eventId, userId);
        return eventService.getEventByUserIdAndEventId(userId, eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}")
    public EventFullDto updateEventByOwner(@PathVariable(name = "userId") @Positive Long userId,
                                           @PathVariable(name = "eventId") @Positive Long eventId,
                                           @RequestBody @Valid EventUpdateDto request) {
        log.info("EventUserController: Request to update event  with id='{}' from user with id='{}', request={}",
                eventId, userId, request);
        return eventService.updateEventByIdAndUserId(eventId, userId, request);
    }

    @GetMapping("/{userId}/events/{eventId}/requests")
    public List<RequestDto> getRequestByEvent(@PathVariable(name = "userId") Long userId,
                                              @PathVariable(name = "eventId") Long eventId) {
        log.info("EventUserController: Request to get requests by eventId='{}' from user with id='{}'",
                eventId, userId);
        return requestService.getRequestsByEventId(userId, eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}/requests")
    public RequestStatusUpdateResponse updateStatusRequest(@PathVariable(name = "userId") @Positive Long userId,
                                                           @PathVariable(name = "eventId") @Positive Long eventId,
                                                           @RequestBody RequestStatusUpdateRequest request) {
        log.info("EventUserController: Request to update status event with id='{}' from user with id='{}'",
                eventId, userId);
        return eventService.updateStatusRequestByEventId(userId, eventId, request);
    }

    @PostMapping("/{userId}/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public RequestDto createRequest(@PathVariable Long userId,
                                    @RequestParam Long eventId) {
        log.info("RequestUserController: Request to create request for participation in event with id={} " +
                "by user with id={}", eventId, userId);
        return requestService.createRequest(userId, eventId);
    }

    @GetMapping("/{userId}/requests")
    public List<RequestDto> getAllRequests(@PathVariable(name = "userId") Long userId) {
        log.info("RequestUserController: Request to get all request by user with id={}", userId);
        return requestService.getAllUserRequestsByUserId(userId);
    }

    @PatchMapping("/{userId}/requests/{requestId}/cancel")
    public RequestDto cancelRequest(@PathVariable(name = "userId") Long userId,
                                    @PathVariable(name = "requestId") Long requestId) {
        log.info("RequestUserController:Request to cancel request for participation with id={} in event " +
                "by user with id={}", requestId, userId);
        return requestService.cancelRequestById(userId, requestId);
    }
}