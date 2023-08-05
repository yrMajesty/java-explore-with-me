package ru.practicum.mainservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.mainservice.entity.Estimation;
import ru.practicum.mainservice.entity.Event;
import ru.practicum.mainservice.exception.AccessException;
import ru.practicum.mainservice.exception.NoFoundObjectException;
import ru.practicum.mainservice.repository.EstimationRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class EstimationService {
    private final EstimationRepository estimationRepository;
    private final EventPublicService eventPublicService;
    private final RequestService requestService;
    private final UserService userService;

    @Transactional
    public void addEventMark(Long userId, Long eventId, Byte mark) {
        Event event = eventPublicService.getEventById(eventId);

        if (Objects.equals(userId, event.getInitiator().getId())) {
            throw new AccessException(String.format("User with id='%s' is initiator of event with id='%s'. " +
                    "Initiator cannot rate the event", userId, eventId));
        }

//        if (event.getEventDate().isAfter(LocalDateTime.now())) {
//            throw new AccessException("You cannot rate the event that has not yet passed");
//        }

        userService.getUserByIdIfExist(userId);
        requestService.checkUserIsConfirmedParticipantEvent(userId, eventId);

        Estimation estimation = Estimation.builder()
                .eventId(eventId)
                .userId(userId)
                .mark(mark)
                .build();

        List<Estimation> allMarks = getAllMarkByEventId(eventId);
        allMarks.add(estimation);

        double newRating = allMarks
                .stream()
                .map(Estimation::getMark)
                .mapToInt(Byte::intValue)
                .average()
                .orElse(0.0);

        event.setRating(newRating);

        estimationRepository.save(estimation);
    }

    @Transactional
    public void deleteEventMark(Long userId, Long eventId) {
        if (!estimationRepository.existsByUserIdAndEventId(userId, eventId)) {
            throw new NoFoundObjectException(
                    String.format("User with id='%s' was not rate the event with id='%s'", userId, eventId));
        }

        estimationRepository.deleteByUserIdAndEventId(userId, eventId);

        double newRating = getAllMarkByEventId(eventId).stream()
                .map(Estimation::getMark)
                .mapToInt(Byte::intValue)
                .average()
                .orElse(0.0);

        eventPublicService.updateRatingById(eventId, newRating);
    }

    public List<Estimation> getAllMarkByEventId(Long eventId) {
        return estimationRepository.findAllMarkByEventId(eventId);
    }

}
