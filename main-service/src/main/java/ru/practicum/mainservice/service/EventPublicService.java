package ru.practicum.mainservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.practicum.client.StatClient;
import ru.practicum.mainservice.dto.event.EventFullDto;
import ru.practicum.mainservice.dto.event.EventSearchDto;
import ru.practicum.mainservice.dto.event.EventShortDto;
import ru.practicum.mainservice.entity.Event;
import ru.practicum.mainservice.entity.enums.EventState;
import ru.practicum.mainservice.exception.EventParametersException;
import ru.practicum.mainservice.exception.NoFoundObjectException;
import ru.practicum.mainservice.repository.EventRepository;
import ru.practicum.mainservice.service.mapper.EventMapper;
import ru.practicum.mainservice.utils.DateTimeUtils;
import ru.practicum.statsdto.HitDto;
import ru.practicum.statsdto.ViewStatsDto;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventPublicService {
    private final EventRepository eventRepository;
    private final StatClient statClient;
    private final EventMapper eventMapper;

    public List<EventShortDto> getAllEvents(EventSearchDto request, String ip, String uri) {
        DateTimeUtils.checkEndIsAfterStart(request.getRangeStart(), request.getRangeEnd());
        saveInfoToStatistics(ip, uri);

        Pageable pageable = PageRequest.of(request.getFrom() / request.getSize(), request.getSize(),
                Sort.by(request.getDirection(), request.getSortBy().toString().toLowerCase()));

        Specification<Event> specification = createRequestForGetEvents(request.getText(), request.getCategories(),
                request.getPaid(), request.getRangeStart(), request.getRangeEnd(), request.getOnlyAvailable());

        List<Event> events = eventRepository.findAll(specification, pageable);
        updateViewsOfEvents(events);

        return eventMapper.toShortDtos(events);
    }

    public Event getEventById(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NoFoundObjectException(String.format("Event with id='%s' not found", eventId)));
    }

    public EventFullDto getEventById(Long eventId, String ip, String uri) {
        Event event = eventRepository.findByIdAndState(eventId, EventState.PUBLISHED)
                .orElseThrow(() -> new NoFoundObjectException(
                        String.format("Event with id='%s' and state 'PUBLISH' not found", eventId)));

        saveInfoToStatistics(ip, uri);
        updateViewsOfEvents(List.of(event));

        return eventMapper.toFullDto(event);
    }

    public List<Event> getEventsByIdIn(List<Long> ids) {
        return eventRepository.findAllByIdIn(ids);
    }

    private Specification<Event> createRequestForGetEvents(String text, List<Long> categories,
                                                           Boolean paid, LocalDateTime startDate, LocalDateTime endDate,
                                                           Boolean onlyAvailable) {
        Specification<Event> specification = Specification.where(null);

        if (Objects.nonNull(text) && StringUtils.hasLength(text)) {
            specification = specification.and((root, query, criteriaBuilder) -> criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("annotation")),
                            "%" + text.toLowerCase() + "%"),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("description")),
                            "%" + text.toLowerCase() + "%")
            ));
        }

        LocalDateTime startDateTime = Objects.requireNonNullElse(startDate, LocalDateTime.now());

        specification = specification.and((root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThan(root.get("eventDate"), startDateTime));

        if (Objects.nonNull(categories) && !categories.isEmpty()) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    root.get("category").get("id").in(categories));
        }

        if (Objects.nonNull(endDate)) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.lessThan(root.get("eventDate"), endDate));
        }

        if (Objects.nonNull(onlyAvailable) && onlyAvailable) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.greaterThanOrEqualTo(root.get("participantLimit"), 0));
        }
        if (Objects.nonNull(paid)) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.greaterThanOrEqualTo(root.get("paid"), paid));
        }

        specification = specification.and((root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("state"), EventState.PUBLISHED));

        return specification;
    }

    private void saveInfoToStatistics(String ip, String uri) {
        statClient.saveInfo(HitDto.builder()
                .app("ewm-main-service")
                .uri(uri)
                .ip(ip)
                .timestamp(LocalDateTime.now().format(DateTimeUtils.DATE_TIME_FORMATTER))
                .build());
    }

    private void updateViewsOfEvents(List<Event> events) {
        List<String> uris = events.stream()
                .map(event -> String.format("/events/%s", event.getId()))
                .collect(Collectors.toList());

        List<ViewStatsDto> statistics = getViewsStatistics(uris);

        events.forEach(event -> {
            ViewStatsDto foundViewInStats = statistics.stream()
                    .filter(statDto -> {
                        Long eventIdFromStats = Long.parseLong(statDto.getUri().substring("/events/".length()));
                        return Objects.equals(eventIdFromStats, event.getId());
                    })
                    .findFirst()
                    .orElse(null);

            long currentCountViews = foundViewInStats == null
                    ? 0
                    : foundViewInStats.getHits();
            event.setViews((int) currentCountViews + 1);
        });

        eventRepository.saveAll(events);
    }

    private List<ViewStatsDto> getViewsStatistics(List<String> uris) {
        return statClient.getStatistics(
                LocalDateTime.now().minusYears(100).format(DateTimeUtils.DATE_TIME_FORMATTER),
                LocalDateTime.now().plusYears(5).format(DateTimeUtils.DATE_TIME_FORMATTER),
                uris,
                true);
    }

    public Event getEventIfExistById(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(() ->
                new NoFoundObjectException(String.format("Event with id='%s' not found", eventId)));
    }

    public void checkUserInitiatorEvent(Long eventId, Long userId) {
        if (!eventRepository.existsByIdAndInitiatorId(eventId, userId)) {
            throw new EventParametersException(String.format("User with id='%s' is not initiator of event with id='%s'",
                    userId, eventId));
        }
    }

    @Transactional
    public void updateRatingById(Long eventId, double newRating) {
        eventRepository.updateRatingById(eventId, newRating);
    }
}