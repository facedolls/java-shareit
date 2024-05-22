package ru.practicum.shareit.request;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoInfo;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ItemRequestMapper {

    @Mapping(target = "items", ignore = true)
    @Mapping(target = "requester", source = "requester")
    ItemRequest toItemRequest(ItemRequestDto itemRequestDto, User requester, LocalDateTime created);

    List<ItemRequestDtoInfo> toItemRequestDtoInfoList(List<ItemRequest> allRequestsUser);

    ItemRequestDtoInfo toItemRequestDtoInfo(ItemRequest itemRequest);
}
