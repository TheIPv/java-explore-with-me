package ru.yandex.practicum.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.user.dto.UserDto;
import ru.yandex.practicum.user.model.NewUserRequest;
import ru.yandex.practicum.user.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/users")
@Validated
@Slf4j
public class AdminUserController {

    private final UserService userService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    List<UserDto> getUsers(@RequestParam(required = false) List<Long> ids,
                                           @RequestParam(required = false, defaultValue = "0") @PositiveOrZero Integer from,
                                           @RequestParam(required = false, defaultValue = "10") @Positive Integer size) {
        log.info("Received GET /admin/users request");
        return userService.getUsers(ids, PageRequest.of(from / size, size));
    }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    UserDto addUser(@Valid @RequestBody NewUserRequest newUserRequest) {
        log.info("Received POST /admin/users request");
        return userService.addUser(newUserRequest);
    }
    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteUserById(@PathVariable Long userId) {
        log.info("Received DELETE /admin/users request");
        userService.deleteUserById(userId);
    }
}
