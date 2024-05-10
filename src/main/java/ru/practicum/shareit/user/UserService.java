package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

public interface UserService {
    UserDto getUserById(Long userId);

    User getById(Long userId);

    Collection<UserDto> getAllUserDto();

    UserDto createUser(UserDto userDto);

    UserDto updateUser(Long userId, UserDto userDto);

    void deleteUserById(Long userId);
}
