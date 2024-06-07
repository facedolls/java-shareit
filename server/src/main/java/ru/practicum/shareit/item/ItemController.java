package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoInfo;

import java.util.Collection;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private static final String USER_ID = "X-Sharer-User-Id";
    private static final String PAGE_FROM = "0";
    private static final String PAGE_SIZE = "10";
    private final ItemService itemService;

    @GetMapping("/{itemId}")
    public ItemDtoInfo getItemById(@PathVariable Long itemId,
                                   @RequestHeader(USER_ID) Long userId) {
        log.info("GET user request id={} for view item id={}", userId, itemId);
        return itemService.getItemDtoById(itemId, userId);
    }

    @GetMapping
    public Collection<ItemDtoInfo> getAllItemUser(@RequestHeader(USER_ID) Long userId,
                                                  @RequestParam(defaultValue = PAGE_FROM) Integer from,
                                                  @RequestParam(defaultValue = PAGE_SIZE) Integer size) {
        log.info("GET user request id={} for view items. Page from={}, page size={}", userId, from, size);
        return itemService.getAllItemUser(userId, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto createItem(@RequestBody ItemDto itemDto,
                              @RequestHeader(USER_ID) Long userId) {
        log.info("POST user request id={} for create item, request body={}", userId, itemDto);
        return itemService.createItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestBody ItemDto itemDto,
                              @PathVariable Long itemId,
                              @RequestHeader(USER_ID) Long userId) {
        log.info("PATCH user request id={} for update item, request body={}", userId, itemDto);
        return itemService.updateItem(itemDto, itemId, userId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> searchItems(@RequestParam String text,
                                           @RequestParam(defaultValue = PAGE_FROM) Integer from,
                                           @RequestParam(defaultValue = PAGE_SIZE) Integer size) {
        log.info("GET request for search item name / description={}", text);
        return itemService.searchItems(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestBody CommentDto commentDto,
                                    @RequestHeader(USER_ID) Long userId,
                                    @PathVariable Long itemId) {
        log.info("POST user request id={} for create comment on item id={}, request body={}",
                userId, itemId, commentDto);
        return itemService.createComment(commentDto, userId, itemId);
    }
}
