package ru.practicum.mainservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.mainservice.entity.Event;
import ru.practicum.mainservice.exception.EventParametersException;
import ru.practicum.mainservice.exception.NoFoundObjectException;
import ru.practicum.mainservice.repository.EventRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;

    public Event getEventIfExistById(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NoFoundObjectException(String.format("Event with id='%s' not found", eventId)));
    }

    public void checkUserInitiatorEvent(Long eventId, Long userId) {
        if (!eventRepository.existsByIdAndInitiatorId(eventId, userId)) {
            throw new EventParametersException(String.format("User with id='%s' is not initiator of event with id='%s'",
                    userId, eventId));
        }
    }

    public List<Event> getEventsByIdIn(List<Long> ids) {
        return eventRepository.findAllByIdIn(ids);
    }
}
