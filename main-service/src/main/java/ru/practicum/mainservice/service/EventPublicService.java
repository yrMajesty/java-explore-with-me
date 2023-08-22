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
import ru.practicum.mainservice.dto.event.enums.EventSortType;
import ru.practicum.mainservice.entity.Event;
import ru.practicum.mainservice.entity.enums.EventState;
import ru.practicum.mainservice.exception.NoFoundObjectException;
import ru.practicum.mainservice.repository.EventRepository;
import ru.practicum.mainservice.service.mapper.EventMapper;
import ru.practicum.mainservice.utils.DateTimeUtils;
import ru.practicum.statsdto.HitDto;
import ru.practicum.statsdto.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventPublicService {
    private final EventRepository eventRepository;
    private final EstimationService estimationService;
    private final StatClient statClient;
    private final EventMapper eventMapper;

    public List<EventShortDto> getAllEvents(EventSearchDto request, String ip, String uri) {
        DateTimeUtils.checkEndIsAfterStart(request.getRangeStart(), request.getRangeEnd());
        saveInfoToStatistics(ip, uri);

        Specification<Event> specification = createRequestForGetEvents(request.getText(), request.getCategories(),
                request.getPaid(), request.getRangeStart(), request.getRangeEnd(), request.getOnlyAvailable());

        if (Objects.equals(request.getSortBy(), EventSortType.RATING)) {
            return getEventsSortByRating(specification, request.getFrom(), request.getSize(), request.getDirection());

        }
        return getEventsSortBy(specification,
                Sort.by(request.getDirection(), request.getSortBy().toString().toLowerCase()),
                request.getFrom(), request.getSize());
    }

    public EventFullDto getEventById(Long eventId, String ip, String uri) {
        Event event = eventRepository.findByIdAndState(eventId, EventState.PUBLISHED)
                .orElseThrow(() -> new NoFoundObjectException(
                        String.format("Event with id='%s' and state 'PUBLISH' not found", eventId)));

        saveInfoToStatistics(ip, uri);
        updateViewsOfEvents(List.of(event));

        Double rating = estimationService.getRatingByEventId(eventId);

        EventFullDto eventDto = eventMapper.toFullDto(event);
        eventDto.setRating(rating);

        return eventDto;
    }

    private List<EventShortDto> getEventsSortByRating(Specification<Event> specification, Integer from, Integer size,
                                                      Sort.Direction direction) {
        Pageable pageable = PageRequest.of(from / size, size);

        List<EventShortDto> eventDtos = getEventsBySpecification(specification, pageable);

        if (Objects.equals(direction, Sort.Direction.ASC)) {
            return eventDtos.stream()
                    .sorted(Comparator.comparing(EventShortDto::getRating))
                    .collect(Collectors.toList());
        }
        return eventDtos.stream()
                .sorted((o1, o2) -> o2.getRating().compareTo(o1.getRating()))
                .collect(Collectors.toList());
    }

    private List<EventShortDto> getEventsSortBy(Specification<Event> specification, Sort sort, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size, sort);

        return getEventsBySpecification(specification, pageable);
    }

    private List<EventShortDto> getEventsBySpecification(Specification<Event> specification, Pageable pageable) {
        List<Event> events = eventRepository.findAll(specification, pageable);

        List<EventShortDto> eventFullDtos = eventMapper.toShortDtos(events);

        Map<Long, Double> ratings = estimationService.getRatingsForEvents(events.stream()
                .map(Event::getId)
                .collect(Collectors.toList()));

        eventFullDtos.forEach(eventFullDto -> eventFullDto.setRating(
                ratings.get(
                        eventFullDto.getId()) == null
                        ? 0.0
                        : ratings.get(eventFullDto.getId())));

        updateViewsOfEvents(events);

        return eventFullDtos;
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
}