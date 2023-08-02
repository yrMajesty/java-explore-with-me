package ru.practicum.mainservice.controller.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainservice.dto.request.RequestDto;
import ru.practicum.mainservice.service.RequestService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class RequestUserController {
    private final RequestService requestService;

    @GetMapping("/{userId}/requests")
    public List<RequestDto> getAllRequests(@PathVariable(name = "userId") Long userId) {
        log.info("Request to get all request by user with id={}", userId);
        return requestService.getAllUserRequestsByUserId(userId);
    }

    @PatchMapping("/{userId}/requests/{requestId}/cancel")
    public RequestDto cancelRequest(@PathVariable(name = "userId") Long userId,
                                    @PathVariable(name = "requestId") Long requestId) {
        log.info("Request to cancel request for participation with id={} in event " +
                "by user with id={}", requestId, userId);
        return requestService.cancelRequestById(userId, requestId);
    }

    @PostMapping("/{userId}/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public RequestDto createRequest(@PathVariable Long userId,
                                    @RequestParam Long eventId) {
        log.info("Request to create request for participation in event with id={} " +
                "by user with id={}", eventId, userId);
        return requestService.createRequest(userId, eventId);
    }
}