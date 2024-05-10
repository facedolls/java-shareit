package ru.practicum.shareit.user;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface UserMapper {
    User toUser(UserDto model);

    UserDto toUserDto(User dto);

    Collection<UserDto> toUserDto(Collection<User> users);
}
