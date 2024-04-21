package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static java.util.stream.Collectors.toList;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserStorage userStorage;
    private final UserMapper mapper;

    public UserDto create(UserDto userDto) {
        return mapper.toUserDto(userStorage.create(mapper.toUser(userDto)));
    }

    public boolean isExistUser(Long userId) {
        return getUserById(userId) != null;
    }

    public UserDto update(UserDto userDto, Long id) {
        if (userDto.getId() == null) {
            userDto.setId(id);
        }
        return mapper.toUserDto(userStorage.update(mapper.toUser(userDto)));
    }

    public UserDto delete(Long userId) {
        return mapper.toUserDto(userStorage.delete(userId));
    }

    public List<UserDto> getUsers() {
        return userStorage.getUsers().stream()
                .map(mapper::toUserDto)
                .collect(toList());
    }

    public UserDto getUserById(Long id) {
        return mapper.toUserDto(userStorage.getUserById(id));
    }
}
