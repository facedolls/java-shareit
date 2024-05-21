package ru.practicum.shareit.request;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoInfo;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;

import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        uses = {UserMapper.class})
public interface ItemRequestMapper {

    ItemRequest toItemRequest(ItemRequestDto itemRequestDto, User requester, LocalDateTime created);

    List<ItemRequestDtoInfo> toItemRequestDtoInfoList(List<ItemRequest> allRequestsUser);

    ItemRequestDtoInfo toItemRequestDtoInfo(ItemRequest itemRequest);
}
