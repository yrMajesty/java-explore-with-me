package ru.practicum.mainservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.practicum.mainservice.dto.event.EventFullDto;
import ru.practicum.mainservice.dto.event.EventUpdateDto;
import ru.practicum.mainservice.entity.Event;
import ru.practicum.mainservice.exception.EventParametersException;
import ru.practicum.mainservice.exception.NoFoundObjectException;
import ru.practicum.mainservice.model.EventState;
import ru.practicum.mainservice.repository.EventRepository;
import ru.practicum.mainservice.service.mapper.EventMapper;
import ru.practicum.mainservice.service.mapper.EventUpdateMapper;
import ru.practicum.mainservice.utils.DateTimeUtils;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class EventAdminService {
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final EventUpdateMapper eventUpdateMapper;

    public List<EventFullDto> getAllEvents(List<Long> users, List<String> states,
                                           List<Long> categories, LocalDateTime startDate, LocalDateTime endDate,
                                           Integer from, Integer size) {
        DateTimeUtils.checkEndIsAfterStart(startDate, endDate);

        Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.ASC, "id"));

        Specification<Event> specification = createRequestForGetEvents(users, states, categories, startDate, endDate);

        List<Event> events = eventRepository.findAll(specification, pageable);
        return eventMapper.toFullDtos(events);
    }

    @Transactional
    public EventFullDto updateEventById(Long eventId, EventUpdateDto request) {
        Event event = getEventByIdIfExist(eventId);

        if (!Objects.equals(EventState.PENDING, event.getState())) {
            throw new EventParametersException("Event status must be 'PENDING'");
        }
        eventUpdateMapper.updateEvent(event, request, true);
        Event updatedEvent = eventRepository.save(event);
        return eventMapper.toFullDto(updatedEvent);
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

    private Event getEventByIdIfExist(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(() ->
                new NoFoundObjectException(String.format("Event with id='%s' not found", eventId)));
    }

}