package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.validated.Create;
import ru.practicum.shareit.validated.Update;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
public class ItemController {
    public static final String USER_ID = "X-Sharer-User-Id";
    public static final String PAGE_FROM = "0";
    public static final String PAGE_SIZE = "10";
    private final ItemClient itemClient;

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@PathVariable @Positive @NotNull Long itemId,
                                              @RequestHeader(USER_ID) Long userId) {
        return itemClient.getItemById(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItemUser(@RequestHeader(USER_ID) Long userId,
                                                 @RequestParam(defaultValue = PAGE_FROM) @Min(0) Integer from,
                                                 @RequestParam(defaultValue = PAGE_SIZE) @Min(1) Integer size) {
        return itemClient.getAllItemUser(userId, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> createItem(@Validated(Create.class) @RequestBody ItemDto itemDto,
                                             @RequestHeader(USER_ID) Long userId) {
        return itemClient.createItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@Validated(Update.class) @RequestBody ItemDto itemDto,
                                             @PathVariable @Positive @NotNull Long itemId,
                                             @RequestHeader(USER_ID) Long userId) {
        return itemClient.updateItem(itemDto, itemId, userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestParam String text,
                                              @RequestParam(defaultValue = PAGE_FROM) @Min(0) Integer from,
                                              @RequestParam(defaultValue = PAGE_SIZE) @Min(1) Integer size) {
        return itemClient.searchItems(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@Valid @RequestBody CommentDto commentDto,
                                                @RequestHeader(USER_ID) Long userId,
                                                @PathVariable @Positive @NotNull Long itemId) {
        return itemClient.createComment(commentDto, userId, itemId);
    }
}
