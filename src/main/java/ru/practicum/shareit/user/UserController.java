package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.service.CheckConsistency;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import java.util.List;

@AllArgsConstructor
@Slf4j
@RestController
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;
    private CheckConsistency check;

    @PostMapping
    public UserDto create(@Valid @RequestBody UserDto userDto) {
        log.info("POST-запрос к /users на добавление пользователя");
        return userService.create(userDto);
    }

    @PatchMapping("/{userId}")
    public UserDto update(@RequestBody UserDto userDto, @PathVariable Long userId) {
        log.info("PATCH-запрос к /users на обновление пользователя с id={}", userId);
        return userService.update(userDto, userId);
    }

    @DeleteMapping("/{userId}")
    public UserDto delete(@PathVariable Long userId) {
        log.info("DELETE-запрос к /users на удаление пользователя с id={}", userId);
        UserDto userDto = userService.delete(userId);
        check.deleteItemsByUser(userId);
        return userDto;
    }

    @GetMapping
    public List<UserDto> getUsers() {
        return userService.getUsers();
    }

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable Long userId) {
        return userService.getUserById(userId);
    }
}
