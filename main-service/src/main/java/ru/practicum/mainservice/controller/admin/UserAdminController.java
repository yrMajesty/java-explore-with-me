package ru.practicum.mainservice.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainservice.dto.user.UserDto;
import ru.practicum.mainservice.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class UserAdminController {
    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@RequestBody @Valid UserDto request) {
        log.info("Request to create a new user {}", request);
        return userService.createUser(request);
    }

    @GetMapping
    public List<UserDto> getUsers(@RequestParam(name = "ids", required = false) List<Long> ids,
                                  @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                  @RequestParam(name = "size", defaultValue = "10") @Positive Integer size) {
        log.info("Request to get all users by id={}", ids);
        return userService.getUsersByIds(ids, from, size);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable(name = "userId") Long userId) {
        log.info("Request to delete a user with id='{}'", userId);
        userService.deleteUserById(userId);
    }
}
