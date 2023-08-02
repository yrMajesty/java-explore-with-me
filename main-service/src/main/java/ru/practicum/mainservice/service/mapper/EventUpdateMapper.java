package ru.practicum.mainservice.service.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.practicum.mainservice.dto.event.EventUpdateDto;
import ru.practicum.mainservice.entity.Category;
import ru.practicum.mainservice.entity.Event;
import ru.practicum.mainservice.entity.enums.EventState;
import ru.practicum.mainservice.service.CategoryService;
import ru.practicum.mainservice.utils.DateTimeUtils;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class EventUpdateMapper {
    private final CategoryService categoryService;
    private final LocationMapper locationMapper;

    public void updateEvent(Event event, EventUpdateDto dto, boolean isAdmin) {
        if (Objects.nonNull(dto.getStateAction())) {
            switch (dto.getStateAction()) {
                case PUBLISH_EVENT:
                    event.setPublishedOn(LocalDateTime.now());
                    event.setState(EventState.PUBLISHED);
                    break;
                case SEND_TO_REVIEW:
                    event.setState(EventState.PENDING);
                    break;
                case REJECT_EVENT:
                case CANCEL_REVIEW:
                    event.setState(EventState.CANCELED);
                    break;
            }
        }

        if (Objects.nonNull(dto.getAnnotation()) && StringUtils.hasLength(dto.getAnnotation())) {
            event.setAnnotation(dto.getAnnotation());
        }
        if (Objects.nonNull(dto.getTitle()) && StringUtils.hasLength(dto.getTitle())) {
            event.setTitle(dto.getTitle());
        }
        if (Objects.nonNull(dto.getCategory())) {
            Category category = categoryService.getCategoryByIdIfExist(dto.getCategory());
            event.setCategory(category);
        }
        if (Objects.nonNull(dto.getDescription()) && StringUtils.hasLength(dto.getDescription())) {
            event.setDescription(dto.getDescription());
        }
        if (Objects.nonNull(dto.getEventDate())) {
            DateTimeUtils.checksPeriodBeforeStartDate(dto.getEventDate(), isAdmin);
            event.setEventDate(dto.getEventDate());
        }
        if (Objects.nonNull(dto.getLocation())) {
            event.setLocation(locationMapper.fromDto(dto.getLocation()));
        }
        if (Objects.nonNull(dto.getPaid())) {
            event.setPaid(dto.getPaid());
        }
        if (Objects.nonNull(dto.getParticipantLimit())) {
            event.setParticipantLimit(dto.getParticipantLimit());
        }
        if (Objects.nonNull(dto.getRequestModeration())) {
            event.setRequestModeration(dto.getRequestModeration());
        }
    }
}