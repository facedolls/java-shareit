package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private static final String PAGE_FROM = "0";
    private static final String PAGE_SIZE = "10";
    private final UserService userService;

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable Long userId) {
        log.info("GET request for view user id={}", userId);
        return userService.getUserById(userId);
    }

    @GetMapping
    public Collection<UserDto> getAllUser(@RequestParam(defaultValue = PAGE_FROM) Integer from,
                                          @RequestParam(defaultValue = PAGE_SIZE) Integer size) {
        log.info("GET request for view users. Page from={}, page size={}", from, size);
        return userService.getAllUsers(from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@RequestBody UserDto userDto) {
        log.info("POST request for create user, request body={}", userDto);
        return userService.createUser(userDto);
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@PathVariable Long userId, @RequestBody UserDto userDto) {
        log.info("PATCH request for update user, request body={}", userDto);
        return userService.updateUser(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public void deleteUserById(@PathVariable Long userId) {
        log.info("DELETE request for delete user with id={}", userId);
        userService.deleteUserById(userId);
    }
}
