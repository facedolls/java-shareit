package ru.practicum.shareit.item;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.dto.BookingDtoInfo;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoInfo;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.util.Collection;
import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ItemMapper {
    @Mapping(target = "id", source = "itemDto.id")
    @Mapping(target = "name", source = "itemDto.name")
    @Mapping(target = "request", ignore = true)
    @Mapping(target = "description", source = "itemDto.description")
    Item toItem(ItemDto itemDto, User owner, ItemRequest itemRequest);

    @Mapping(target = "requestId", ignore = true)
    ItemDto toItemDto(Item item);

    @Mapping(target = "lastBooking", ignore = true)
    @Mapping(target = "nextBooking", ignore = true)
    @Mapping(target = "comments", source = "commentDto")
    ItemDtoInfo toOneItemDtoInfoForAllUsers(Item item, List<CommentDto> commentDto);

    @Mapping(target = "id", source = "item.id")
    @Mapping(target = "lastBooking", source = "last")
    @Mapping(target = "nextBooking", source = "next")
    ItemDtoInfo toOneItemDtoInfoForOwner(Item item, BookingDtoInfo next, BookingDtoInfo last,
                                         List<CommentDto> comments);

    Collection<ItemDto> toItemDtoCollection(Collection<Item> items);
}
