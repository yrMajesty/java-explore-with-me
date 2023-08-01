package ru.practicum.mainservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.mainservice.dto.category.CategoryDto;
import ru.practicum.mainservice.dto.compilation.CompilationDto;
import ru.practicum.mainservice.dto.event.EventFullDto;
import ru.practicum.mainservice.dto.event.EventShortDto;
import ru.practicum.mainservice.service.CategoryService;
import ru.practicum.mainservice.service.CompilationService;
import ru.practicum.mainservice.service.EventPublicService;
import ru.practicum.mainservice.utils.DateTimeUtils;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PublicController {
    private final CategoryService categoryService;
    private final CompilationService compilationService;
    private final EventPublicService eventService;

    @GetMapping("/categories")
    public List<CategoryDto> getCategories(@RequestParam(name = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                           @RequestParam(name = "size", defaultValue = "10") @Positive Integer size) {
        log.info("CategoryPublicController: Request to get all categories");
        return categoryService.getAllCategories(from, size);
    }

    @GetMapping("/categories/{catId}")
    public CategoryDto getCategoryById(@PathVariable(name = "catId") @Positive Long categoryId) {
        log.info("CategoryPublicController: Request to get category with id='{}'", categoryId);
        return categoryService.getCategoryById(categoryId);
    }

    @GetMapping("/compilations")
    public List<CompilationDto> getComplications(@RequestParam(required = false) Boolean pinned,
                                                 @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                 @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("CompilationPublicController: Request to get all compilations");
        return compilationService.getAllCompilations(pinned, from, size);
    }

    @GetMapping("/compilations/{compId}")
    public CompilationDto getCompilation(@PathVariable(name = "compId") @Positive Long compilationId) {
        log.info("CompilationPublicController: Request to get compilation with id='{}'", compilationId);
        return compilationService.getCompilationById(compilationId);
    }

    @GetMapping("/events")
    public List<EventShortDto> getEvents(
            @RequestParam(name = "text", required = false) String text,
                                         @RequestParam(name = "categories", required = false) List<Long> categories,
                                         @RequestParam(name = "paid", required = false) Boolean paid,
                                         @RequestParam(name = "rangeStart", required = false)
                                         @DateTimeFormat(pattern = DateTimeUtils.DATE_TIME_FORMAT) LocalDateTime startDate,
                                         @RequestParam(name = "rangeEnd", required = false)
                                         @DateTimeFormat(pattern = DateTimeUtils.DATE_TIME_FORMAT) LocalDateTime endDate,
                                         @RequestParam(name = "onlyAvailable", required = false) Boolean onlyAvailable,
                                         @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                         @RequestParam(name = "size", defaultValue = "10") @Positive Integer size,
                                         HttpServletRequest request) {
        log.info("EventPublicController: Request to receive all events with request parameters: text={}, " +
                        "categories={}, paid={}, rangeStart={}, rangeEnd={}, onlyAvailable={},  from={}, " +
                        "size={}, request={}",
                text, categories, paid, startDate, endDate, onlyAvailable, from, size, request.getRequestURI());

        return eventService.getAllEvents(text, categories, paid, startDate, endDate, onlyAvailable,
                from, size, request.getRemoteAddr(), request.getRequestURI());
    }

    @GetMapping("/events/{eventId}")
    public EventFullDto getEvent(@PathVariable(name = "eventId") @Positive Long eventId,
                                 HttpServletRequest request) {
        log.info("EventPublicController: Request to get compilation with id='{}'", eventId);
        return eventService.getEventById(eventId, request.getRemoteAddr(), request.getRequestURI());
    }
}
