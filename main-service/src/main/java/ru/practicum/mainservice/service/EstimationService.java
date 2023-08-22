package ru.practicum.mainservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.mainservice.dto.db.EventMarkRequestResult;
import ru.practicum.mainservice.entity.Estimation;
import ru.practicum.mainservice.entity.Event;
import ru.practicum.mainservice.exception.AccessException;
import ru.practicum.mainservice.exception.NoFoundObjectException;
import ru.practicum.mainservice.repository.EstimationRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EstimationService {
    private final EstimationRepository estimationRepository;
    private final EventService eventService;
    private final RequestService requestService;
    private final UserService userService;

    @Transactional
    public void addEventMark(Long userId, Long eventId, Byte mark) {
        Event event = eventService.getEventIfExistById(eventId);

        if (Objects.equals(userId, event.getInitiator().getId())) {
            throw new AccessException(String.format("User with id='%s' is initiator of event with id='%s'. " +
                    "Initiator cannot rate the event", userId, eventId));
        }

        userService.getUserByIdIfExist(userId);
        requestService.checkUserIsConfirmedParticipantEvent(userId, eventId);

        Estimation estimation = Estimation.builder()
                .eventId(eventId)
                .userId(userId)
                .mark(mark)
                .build();

        estimationRepository.save(estimation);
    }

    @Transactional
    public void deleteEventMark(Long userId, Long eventId) {
        if (!estimationRepository.existsByUserIdAndEventId(userId, eventId)) {
            throw new NoFoundObjectException(
                    String.format("User with id='%s' was not rate the event with id='%s'", userId, eventId));
        }

        estimationRepository.deleteByUserIdAndEventId(userId, eventId);
    }

    public Double getRatingByEventId(Long id) {
        return estimationRepository.getRatingByEventId(id).orElse(0.0);
    }

    public Map<Long, Double> getRatingsForEvents(List<Long> ids) {
        return estimationRepository.findAvgMarkByEventIdIn(ids)
                .stream()
                .collect(Collectors.toMap(
                        EventMarkRequestResult::getEventId,
                        EventMarkRequestResult::getMark
                ));
    }
}
