package ru.practicum.mainservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.mainservice.dto.event.EventFullDto;
import ru.practicum.mainservice.dto.event.EventNewDto;
import ru.practicum.mainservice.dto.event.EventShortDto;
import ru.practicum.mainservice.dto.event.EventUpdateDto;
import ru.practicum.mainservice.dto.event.enums.EventSortType;
import ru.practicum.mainservice.dto.request.RequestDto;
import ru.practicum.mainservice.dto.request.RequestStatusUpdateRequest;
import ru.practicum.mainservice.dto.request.RequestStatusUpdateResponse;
import ru.practicum.mainservice.entity.Category;
import ru.practicum.mainservice.entity.Event;
import ru.practicum.mainservice.entity.Request;
import ru.practicum.mainservice.entity.User;
import ru.practicum.mainservice.entity.enums.EventState;
import ru.practicum.mainservice.entity.enums.RequestStatus;
import ru.practicum.mainservice.exception.EventParametersException;
import ru.practicum.mainservice.exception.NoFoundObjectException;
import ru.practicum.mainservice.repository.EventRepository;
import ru.practicum.mainservice.service.mapper.EventMapper;
import ru.practicum.mainservice.service.mapper.EventUpdateMapper;
import ru.practicum.mainservice.service.mapper.RequestMapper;
import ru.practicum.mainservice.utils.DateTimeUtils;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventUserService {
    private final EventRepository eventRepository;
    private final UserService userService;
    private final CategoryService categoryService;
    private final RequestService requestService;
    private final EventMapper eventMapper;
    private final EventUpdateMapper eventUpdateMapper;
    private final RequestMapper requestMapper;
    private final EstimationService estimationService;

    @Transactional
    public EventFullDto createEvent(EventNewDto request, Long userId) {
        DateTimeUtils.checksPeriodBeforeStartDate(request.getEventDate(), false);

        User user = userService.getUserByIdIfExist(userId);
        Category category = categoryService.getCategoryByIdIfExist(request.getCategory());

        Event event = eventMapper.fromDto(request, category, user);
        Event savedEvent = eventRepository.save(event);

        EventFullDto eventDto = eventMapper.toFullDto(savedEvent);
        Double rating = estimationService.getRatingByEventId(event.getId());
        eventDto.setRating(rating);

        return eventDto;
    }

    public List<EventShortDto> getEventsByUserId(Long userId, Integer from, Integer size,
                                                 EventSortType sortBy, Sort.Direction direction) {
        userService.checkExistUserById(userId);

        Pageable pageable;
        if (Objects.equals(sortBy, EventSortType.RATING)) {
            pageable = PageRequest.of(from / size, size);
        } else {
            pageable = PageRequest.of(from / size, size, Sort.by(direction, sortBy.toString().toLowerCase()));
        }

        List<Event> events = eventRepository.findAllByInitiatorId(userId, pageable);
        List<EventShortDto> eventShortDtos = eventMapper.toShortDtos(events);

        Map<Long, Double> ratings = estimationService.getRatingsForEvents(events.stream()
                .map(Event::getId)
                .collect(Collectors.toList()));

        eventShortDtos.forEach(eventFullDto -> eventFullDto.setRating(
                ratings.get(
                        eventFullDto.getId()) == null
                        ? 0.0
                        : ratings.get(eventFullDto.getId())));

        return eventShortDtos;
    }

    public EventFullDto getEventByUserIdAndEventId(Long userId, Long eventId) {
        userService.checkExistUserById(userId);

        Event event = getEventByIdAndInitiatorIdIfExist(eventId, userId);
        EventFullDto eventDto = eventMapper.toFullDto(event);

        Double rating = estimationService.getRatingByEventId(eventId);
        eventDto.setRating(rating);

        return eventDto;
    }

    public EventFullDto updateEventByIdAndUserId(Long eventId, Long userId, EventUpdateDto request) {
        userService.checkExistUserById(userId);

        Event foundEvent = getEventByIdAndInitiatorIdIfExist(eventId, userId);

        if (!Objects.equals(userId, foundEvent.getInitiator().getId())) {
            throw new EventParametersException(String.format("User with id='%s' is not initiator of event with id='%s'",
                    userId, eventId));
        }

        if (Objects.equals(EventState.PUBLISHED, foundEvent.getState())) {
            throw new EventParametersException("Event state is 'PUBLISHED'. " +
                    "Event state must be is 'PENDING' or 'CANCELED'");
        }

        eventUpdateMapper.updateEvent(foundEvent, request, false);

        Event updatedEvent = eventRepository.save(foundEvent);
        EventFullDto eventDto = eventMapper.toFullDto(updatedEvent);

        Double rating = estimationService.getRatingByEventId(eventId);
        eventDto.setRating(rating);

        return eventDto;
    }

    @Transactional
    public RequestStatusUpdateResponse updateStatusRequestByEventId(Long userId, Long eventId,
                                                                    RequestStatusUpdateRequest request) {
        userService.checkExistUserById(userId);
        Event event = getEventByIdAndInitiatorIdIfExist(eventId, userId);

        if (event.getConfirmedRequests() >= event.getParticipantLimit()) {
            throw new EventParametersException("The limit of participation in the event has been reached");
        }

        List<RequestDto> confirmed = new ArrayList<>();
        List<RequestDto> rejected = new ArrayList<>();

        List<Request> requests = requestService.getAllById(request.getRequestIds());

        requests.forEach(req -> {
            if (!Objects.equals(req.getStatus(), RequestStatus.PENDING)) {
                throw new EventParametersException("The status can be changed only for requests with 'WAITING' status");
            }

            if (event.getParticipantLimit() == 0 ||
                    (event.getParticipantLimit() > event.getConfirmedRequests() && (!event.getRequestModeration() ||
                            (Objects.equals(request.getStatus(), RequestStatus.CONFIRMED))))) {
                req.setStatus(RequestStatus.CONFIRMED);
                event.setConfirmedRequests(event.getConfirmedRequests() + 1);
                confirmed.add(requestMapper.toDto(req));

            } else {
                req.setStatus(RequestStatus.REJECTED);
                rejected.add(requestMapper.toDto(req));
            }
        });

        eventRepository.save(event);
        return new RequestStatusUpdateResponse(confirmed, rejected);
    }

    public Event getEventByIdAndInitiatorIdIfExist(Long eventId, Long userId) {
        return eventRepository.findByIdAndInitiatorId(eventId, userId).orElseThrow(() ->
                new NoFoundObjectException(String.format("Event with id='%s' and initiator with id='%s' not found",
                        eventId, userId)));
    }
}