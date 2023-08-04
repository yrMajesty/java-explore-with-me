package ru.practicum.mainservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.mainservice.dto.request.RequestDto;
import ru.practicum.mainservice.entity.Event;
import ru.practicum.mainservice.entity.Request;
import ru.practicum.mainservice.entity.User;
import ru.practicum.mainservice.exception.EventParametersException;
import ru.practicum.mainservice.exception.InvalidRequestException;
import ru.practicum.mainservice.exception.NoFoundObjectException;
import ru.practicum.mainservice.entity.enums.EventState;
import ru.practicum.mainservice.entity.enums.RequestStatus;
import ru.practicum.mainservice.repository.RequestRepository;
import ru.practicum.mainservice.service.mapper.RequestMapper;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RequestService {
    private final RequestRepository requestRepository;
    private final UserService userService;
    private final EventPublicService eventService;
    private final RequestMapper requestMapper;

    @Transactional
    public RequestDto createRequest(Long userId, Long eventId) {
        User user = userService.getUserByIdIfExist(userId);
        Event event = eventService.getEventIfExistById(eventId);

        if (Objects.equals(userId, event.getInitiator().getId())) {
            throw new EventParametersException(String.format("User with id='%s' is not initiator event with id='%s' ",
                    userId, eventId));
        }

        checkNotExistRequestByUserIdAndEventId(userId, eventId);

        if (!Objects.equals(event.getState(), EventState.PUBLISHED)) {
            throw new EventParametersException(String.format("Status of event with id='%s' is not 'PUBLISHED'", eventId));
        }

        Request request = Request.builder()
                .created(LocalDateTime.now())
                .status(RequestStatus.PENDING)
                .event(event)
                .requester(user)
                .build();

        int confirmed = event.getConfirmedRequests();
        int limit = event.getParticipantLimit();

        if (limit == 0) {
            event.setConfirmedRequests(confirmed + 1);
            request.setStatus(RequestStatus.CONFIRMED);
        } else if (confirmed < limit) {
            if (!event.getRequestModeration()) {
                event.setConfirmedRequests(confirmed + 1);
                request.setStatus(RequestStatus.PENDING);
            }
        } else {
            throw new EventParametersException(String.format("There are no free places to events with id='%s'",
                    eventId));
        }

        Request savedRequest = requestRepository.save(request);
        return requestMapper.toDto(savedRequest);
    }

    public List<RequestDto> getAllUserRequestsByUserId(Long userId) {
        userService.checkExistUserById(userId);

        List<Request> requests = requestRepository.findAllByRequesterId(userId);
        return requestMapper.toDtos(requests);
    }

    @Transactional
    public RequestDto cancelRequestById(Long userId, Long requestId) {
        userService.checkExistUserById(userId);

        Request request = getRequestIfExistByIdAndRequesterId(requestId, userId);

        if ((Objects.equals(request.getStatus(), RequestStatus.CANCELED))
                || (Objects.equals(request.getStatus(), RequestStatus.REJECTED))) {
            throw new InvalidRequestException("Request already was canceled");
        }

        request.setStatus(RequestStatus.CANCELED);
        Request savedRequest = requestRepository.save(request);
        return requestMapper.toDto(savedRequest);
    }

    private Request getRequestIfExistByIdAndRequesterId(Long requestId, Long userId) {
        return requestRepository.findByIdAndRequesterId(requestId, userId).orElseThrow(() ->
                new NoFoundObjectException(String.format("Request with id='%s' and requester with id='%s' not found",
                        requestId, userId)));
    }

    public List<RequestDto> getRequestsByEventId(Long userId, Long eventId) {
        userService.checkExistUserById(userId);

        eventService.isUserInitiatorEvent(eventId, userId);

        List<Request> requests = requestRepository.findAllByEventId(eventId);
        return requestMapper.toDtos(requests);
    }

    public List<Request> getAllById(Set<Long> ids) {
        return requestRepository.findAllById(ids);
    }

    private void checkNotExistRequestByUserIdAndEventId(Long userId, Long eventId) {
        if (!requestRepository.findByRequesterIdAndEventId(userId, eventId).isEmpty()) {
            throw new EventParametersException("Request already was created");
        }
    }
}
