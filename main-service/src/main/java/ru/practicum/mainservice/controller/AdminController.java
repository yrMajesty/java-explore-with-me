package ru.practicum.mainservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainservice.dto.category.CategoryDto;
import ru.practicum.mainservice.dto.compilation.CompilationDto;
import ru.practicum.mainservice.dto.compilation.CompilationNewDto;
import ru.practicum.mainservice.dto.compilation.CompilationUpdateRequest;
import ru.practicum.mainservice.dto.event.EventFullDto;
import ru.practicum.mainservice.dto.event.EventUpdateDto;
import ru.practicum.mainservice.dto.user.UserDto;
import ru.practicum.mainservice.service.CategoryService;
import ru.practicum.mainservice.service.CompilationService;
import ru.practicum.mainservice.service.EventAdminService;
import ru.practicum.mainservice.service.UserService;
import ru.practicum.mainservice.utils.DateTimeUtils;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final UserService userService;
    private final CategoryService categoryService;
    private final EventAdminService eventService;
    private final CompilationService compilationService;

    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@RequestBody @Valid UserDto request) {
        log.info("UserAdminController: Request to create a new user {}", request);
        return userService.createUser(request);
    }

    @GetMapping("/users")
    public List<UserDto> getUsers(@RequestParam(name = "ids", required = false) List<Long> ids,
                                  @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                  @RequestParam(name = "size", defaultValue = "10") @Positive Integer size) {
        log.info("UserAdminController: Request to get all users by id={}", ids);
        return userService.getUsersByIds(ids, from, size);
    }

    @DeleteMapping("/users/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable(name = "userId") Long userId) {
        log.info("UserAdminController: Request to delete a user with id='{}'", userId);
        userService.deleteUserById(userId);
    }

    @PostMapping("/categories")
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto createCategory(@RequestBody @Valid CategoryDto request) {
        log.info("CategoryAdminController: Request to create a category {}", request);
        return categoryService.createCategory(request);
    }

    @DeleteMapping("/categories/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable(name = "catId") @Positive Long categoryId) {
        log.info("CategoryAdminController: Request to delete a category with id='{}'", categoryId);
        categoryService.deleteCategoryById(categoryId);
    }

    @PatchMapping("/categories/{catId}")
    public CategoryDto updateCategory(@PathVariable(name = "catId") @Positive Long categoryId,
                                      @RequestBody @Valid CategoryDto request) {
        log.info("CategoryAdminController: Request to update a category with id='{}', new parameters={}", categoryId, request);
        return categoryService.updateCategoryById(categoryId, request);
    }

    @GetMapping("/events")
    public List<EventFullDto> getEvents(@RequestParam(name = "users", required = false) List<Long> users,
                                        @RequestParam(name = "states", required = false) List<String> states,
                                        @RequestParam(name = "categories", required = false) List<Long> categories,
                                        @RequestParam(name = "rangeStart", required = false)
                                        @DateTimeFormat(pattern = DateTimeUtils.DATE_TIME_FORMAT) LocalDateTime startDate,
                                        @RequestParam(name = "rangeEnd", required = false)
                                        @DateTimeFormat(pattern = DateTimeUtils.DATE_TIME_FORMAT) LocalDateTime endDate,
                                        @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                        @RequestParam(name = "size", defaultValue = "10") @Positive Integer size) {
        log.info("EventAdminController: Request to get all events with parameters:" +
                        "users={}, states={}, categories={}, rangeStart={}, rangeEnd={}, from={}, size={}",
                users, states, categories, startDate, endDate, from, size);
        return eventService.getAllEvents(users, states, categories, startDate, endDate, from, size);
    }

    @PatchMapping("/events/{eventId}")
    public EventFullDto updateEvent(@PathVariable(name = "eventId") Long eventId,
                                    @RequestBody @Valid EventUpdateDto request) {
        log.info("EventAdminController: Event update request with id='{}', new parameters={}", eventId, request);
        return eventService.updateEventById(eventId, request);
    }

    @PostMapping("/compilations")
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto createCompilation(@RequestBody @Valid CompilationNewDto request) {
        log.info("CompilationAdminController: Request to create a new collection {}", request);
        return compilationService.createCompilation(request);
    }

    @PatchMapping("/compilations/{compId}")
    public CompilationDto updateCompilation(@PathVariable(name = "compId") @Positive Long compilationId,
                                            @RequestBody @Valid CompilationUpdateRequest request) {
        log.info("CompilationAdminController: Request to update compilation with id='{}', new parameters={}",
                compilationId, request);
        return compilationService.updateCompilationById(compilationId, request);
    }

    @DeleteMapping("/compilations/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable(name = "compId") @Positive Long compilationId) {
        log.info("CompilationAdminController: Request to delete compilation with id='{}'", compilationId);
        compilationService.deleteCompilationById(compilationId);
    }
}
