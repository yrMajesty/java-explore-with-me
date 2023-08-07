package ru.practicum.mainservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.practicum.mainservice.dto.event.EventFullDto;
import ru.practicum.mainservice.dto.event.EventSearchDto;
import ru.practicum.mainservice.dto.event.EventUpdateDto;
import ru.practicum.mainservice.dto.event.enums.EventSortType;
import ru.practicum.mainservice.entity.Event;
import ru.practicum.mainservice.entity.enums.EventState;
import ru.practicum.mainservice.exception.EventParametersException;
import ru.practicum.mainservice.repository.EventRepository;
import ru.practicum.mainservice.service.mapper.EventMapper;
import ru.practicum.mainservice.service.mapper.EventUpdateMapper;
import ru.practicum.mainservice.utils.DateTimeUtils;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventAdminService {
    private final EventRepository eventRepository;
    private final EventService eventService;
    private final EstimationService estimationService;
    private final EventMapper eventMapper;
    private final EventUpdateMapper eventUpdateMapper;

    public List<EventFullDto> getAllEvents(EventSearchDto request) {
        DateTimeUtils.checkEndIsAfterStart(request.getRangeStart(), request.getRangeEnd());

        Specification<Event> specification = createRequestForGetEvents(request.getUsers(), request.getStates(),
                request.getCategories(), request.getRangeStart(), request.getRangeEnd());

        if (Objects.equals(request.getSortBy(), EventSortType.RATING)) {
            return getEventsSortByRating(specification, request.getFrom(), request.getSize(), request.getDirection());
        }
        return getEventsSortBy(specification,
                Sort.by(request.getDirection(), request.getSortBy().toString().toLowerCase()),
                request.getFrom(), request.getSize());

    }

    @Transactional
    public EventFullDto updateEventById(Long eventId, EventUpdateDto request) {
        Event event = eventService.getEventIfExistById(eventId);

        if (!Objects.equals(EventState.PENDING, event.getState())) {
            throw new EventParametersException("Event status must be 'PENDING'");
        }
        eventUpdateMapper.updateEvent(event, request, true);
        Event updatedEvent = eventRepository.save(event);

        return eventMapper.toFullDto(updatedEvent);
    }

    private List<EventFullDto> getEventsSortByRating(Specification<Event> specification, Integer from, Integer size,
                                                     Sort.Direction direction) {
        Pageable pageable = PageRequest.of(from / size, size);

        List<EventFullDto> eventFullDtos = getEventsBySpecification(specification, pageable);

        if (Objects.equals(direction, Sort.Direction.ASC)) {
            return eventFullDtos.stream()
                    .sorted(Comparator.comparing(EventFullDto::getRating))
                    .collect(Collectors.toList());
        }
        return eventFullDtos.stream()
                .sorted((o1, o2) -> o2.getRating().compareTo(o1.getRating()))
                .collect(Collectors.toList());
    }

    private List<EventFullDto> getEventsBySpecification(Specification<Event> specification, Pageable pageable) {
        List<Event> events = eventRepository.findAll(specification, pageable);
        List<EventFullDto> eventFullDtos = eventMapper.toFullDtos(events);

        Map<Long, Double> ratings = estimationService.getRatingsForEvents(events.stream()
                .map(Event::getId)
                .collect(Collectors.toList()));

        eventFullDtos.forEach(eventFullDto -> eventFullDto.setRating(
                ratings.get(
                        eventFullDto.getId()) == null
                        ? 0.0
                        : ratings.get(eventFullDto.getId())));
        return eventFullDtos;
    }

    private List<EventFullDto> getEventsSortBy(Specification<Event> specification, Sort sort, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size, sort);

        return getEventsBySpecification(specification, pageable);
    }

    private Specification<Event> createRequestForGetEvents(List<Long> users, List<String> states, List<Long> categories,
                                                           LocalDateTime startDate, LocalDateTime endDate) {
        Specification<Event> specification = Specification.where(null);

        if (Objects.nonNull(users) && !users.isEmpty()) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    root.get("initiator").get("id").in(users));
        }
        if (Objects.nonNull(states) && !states.isEmpty()) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    root.get("state").as(String.class).in(states));
        }
        if (Objects.nonNull(categories) && !categories.isEmpty()) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    root.get("category").get("id").in(categories));
        }
        if (Objects.nonNull(startDate)) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.greaterThanOrEqualTo(root.get("eventDate"), startDate));
        }
        if (Objects.nonNull(endDate)) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.lessThanOrEqualTo(root.get("eventDate"), endDate));
        }
        return specification;
    }
}