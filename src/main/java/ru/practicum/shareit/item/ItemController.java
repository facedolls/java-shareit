package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoInfo;
import ru.practicum.shareit.validated.Create;
import ru.practicum.shareit.validated.Update;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.Collection;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
public class ItemController {
    private static final String USER_ID = "X-Sharer-User-Id";
    private final ItemService itemService;

    @GetMapping("/{itemId}")
    public ItemDtoInfo getItemById(@PathVariable @Positive @NotNull Long itemId,
                                   @RequestHeader(USER_ID) Long userId) {
        return itemService.getItemDtoById(itemId, userId);
    }

    @GetMapping
    public Collection<ItemDtoInfo> getAllItemUser(@RequestHeader(USER_ID) Long userId) {
        return itemService.getAllItemUser(userId);
    }

    @PostMapping
    public ItemDto createItem(@Validated(Create.class) @RequestBody ItemDto itemDto,
                              @RequestHeader(USER_ID) Long userId) {
        return itemService.createItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@Validated(Update.class) @RequestBody ItemDto itemDto,
                              @PathVariable @Positive @NotNull Long itemId,
                              @RequestHeader(USER_ID) Long userId) {
        return itemService.updateItem(userId, itemId, itemDto);
    }

    @GetMapping("/search")
    public Collection<ItemDto> searchItems(@RequestParam String text) {
        return itemService.searchItems(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@Valid @RequestBody CommentDto commentDto,
                                    @RequestHeader(USER_ID) Long userId,
                                    @PathVariable @Positive @NotNull Long itemId) {
        return itemService.createComment(commentDto, userId, itemId);
    }
}
