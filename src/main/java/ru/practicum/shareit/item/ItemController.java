package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoInfo;
import ru.practicum.shareit.validated.Create;
import ru.practicum.shareit.validated.Update;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.Collection;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
public class ItemController {
    public static final String USER_ID = "X-Sharer-User-Id";
    private final ItemService itemService;

    @GetMapping("/{itemId}")
    public ItemDtoInfo getItemById(@PathVariable @Positive @NotNull Long itemId,
                                   @RequestHeader(USER_ID) Long userId) {
        return itemService.getItemDtoById(itemId, userId);
    }

    @GetMapping
    public Collection<ItemDtoInfo> getAllItemUser(@RequestHeader(USER_ID) Long userId,
                                                  @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                                  @RequestParam(defaultValue = "10") @Min(1) Integer size) {
        return itemService.getAllItemUser(userId, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto createItem(@Validated(Create.class) @RequestBody ItemDto itemDto,
                              @RequestHeader(USER_ID) Long userId) {
        return itemService.createItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@Validated(Update.class) @RequestBody ItemDto itemDto,
                              @PathVariable @Positive @NotNull Long itemId,
                              @RequestHeader(USER_ID) Long userId) {
        return itemService.updateItem(itemDto, itemId, userId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> searchItems(@RequestParam String text,
                                           @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                           @RequestParam(defaultValue = "10") @Min(1) Integer size) {
        return itemService.searchItems(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@Valid @RequestBody CommentDto commentDto,
                                    @RequestHeader(USER_ID) Long userId,
                                    @PathVariable @Positive @NotNull Long itemId) {
        return itemService.createComment(commentDto, userId, itemId);
    }
}
