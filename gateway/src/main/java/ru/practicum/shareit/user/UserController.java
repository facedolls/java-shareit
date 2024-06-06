package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.validated.Create;
import ru.practicum.shareit.validated.Update;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Validated
public class UserController {
    private static final String PAGE_FROM = "0";
    private static final String PAGE_SIZE = "10";
    private final UserClient userClient;

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUserById(@PathVariable @Positive @NotNull Long userId) {
        return userClient.getUserById(userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUser(@RequestParam(defaultValue = PAGE_FROM) @Min(0) Integer from,
                                             @RequestParam(defaultValue = PAGE_SIZE) @Min(1) Integer size) {
        return userClient.getAllUser(from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> createUser(@Validated(Create.class) @RequestBody UserDto userDto) {
        return userClient.createUser(userDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@PathVariable @Positive @NotNull Long userId,
                                             @Validated(Update.class) @RequestBody UserDto userDto) {
        return userClient.updateUser(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUserById(@PathVariable @Positive @NotNull Long userId) {
        return userClient.deleteUserById(userId);
    }
}
