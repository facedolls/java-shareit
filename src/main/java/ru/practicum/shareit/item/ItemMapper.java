package ru.practicum.shareit.item;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDtoInfo;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoInfo;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ItemMapper {
    Item toItem(ItemDto model, User owner);

    ItemDto toItemDto(Item dto);

    ItemDtoInfo toItemDtoInfo(Item item, Map<Long, List<CommentDto>> comments);

    ItemDtoInfo toItemDtoInfo(Item item, BookingDtoInfo next, BookingDtoInfo last, List<CommentDto> comments);

    Collection<ItemDto> toItemDto(Collection<Item> items);
}
