package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;

import java.util.List;

import static java.util.stream.Collectors.toList;

@RequiredArgsConstructor
@Service
public class ItemService {
    private final ItemStorage itemStorage;
    private final ItemMapper mapper;
    private final UserService userServiceChecker;

    public ItemDto create(ItemDto itemDto, Long ownerId) {
        if (userServiceChecker.isExistUser(ownerId)) {
            return mapper.toItemDto(itemStorage.create(mapper.toItem(itemDto, ownerId)));
        } else {
            return null;
        }
    }

    public List<ItemDto> getItemsByOwner(Long ownderId) {
        return itemStorage.getItemsByOwner(ownderId).stream()
                .map(mapper::toItemDto)
                .collect(toList());
    }

    public ItemDto getItemById(Long id) {
        return mapper.toItemDto(itemStorage.getItemById(id));
    }

    public ItemDto update(ItemDto itemDto, Long ownerId, Long itemId) {
        if (itemDto.getId() == null) {
            itemDto.setId(itemId);
        }
        Item oldItem = itemStorage.getItemById(itemId);
        if (!oldItem.getOwnerId().equals(ownerId)) {
            throw new NotFoundException("У пользователя нет такой вещи!");
        }
        return mapper.toItemDto(itemStorage.update(mapper.toItem(itemDto, ownerId)));
    }

    public ItemDto delete(Long itemId, Long ownerId) {
        Item item = itemStorage.getItemById(itemId);
        if (!item.getOwnerId().equals(ownerId)) {
            throw new NotFoundException("У пользователя нет такой вещи!");
        }
        return mapper.toItemDto(itemStorage.delete(itemId));
    }

    public void deleteItemsByOwner(Long ownderId) {
        itemStorage.deleteItemsByOwner(ownderId);
    }

    public List<ItemDto> getItemsBySearchQuery(String text) {
        text = text.toLowerCase();
        return itemStorage.getItemsBySearchQuery(text).stream()
                .map(mapper::toItemDto)
                .collect(toList());
    }
}
