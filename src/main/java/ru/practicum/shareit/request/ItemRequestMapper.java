package ru.practicum.shareit.request;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoInfo;
import ru.practicum.shareit.user.User;

import javax.annotation.Generated;
import java.time.LocalDateTime;
import java.util.List;

@lombok.Generated
@Generated("org.mapstruct")
@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        uses = {ItemMapper.class})
public interface ItemRequestMapper {

    @Mapping(target = "description", source = "itemRequestDto.description")
    @Mapping(target = "items", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "requester", source = "requester")
    @Mapping(target = "created", source = "created")
    ItemRequest toItemRequest(ItemRequestDto itemRequestDto, User requester, LocalDateTime created);

    List<ItemRequestDtoInfo> toItemRequestDtoInfoList(List<ItemRequest> allRequestsUser);

    ItemRequestDtoInfo toItemRequestDtoInfo(ItemRequest itemRequest);
}
