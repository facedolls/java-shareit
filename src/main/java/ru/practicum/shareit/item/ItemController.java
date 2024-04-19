package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.service.CheckConsistency;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/items")
public class ItemController {
    private static final String OWNER = "X-Sharer-User-Id";
    private final ItemService itemService;

    private CheckConsistency check;


    @Autowired
    public ItemController(ItemService itemService, CheckConsistency checkConsistency) {
        this.itemService = itemService;
        this.check = checkConsistency;
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable Long itemId) {
        log.info("GET-запрос к /items на получение вещи с id={}", itemId);
        return itemService.getItemById(itemId);
    }

    @ResponseBody
    @PostMapping
    public ItemDto create(@Valid @RequestBody ItemDto itemDto, @RequestHeader(OWNER) Long ownerId) {
        log.info("POST-запрос к /items на добавление вещи владельцем с id={}", ownerId);
        ItemDto newItemDto = null;
        if (check.isExistUser(ownerId)) {
            newItemDto = itemService.create(itemDto, ownerId);
        }
        return newItemDto;
    }

    @GetMapping
    public List<ItemDto> getItemsByOwner(@RequestHeader(OWNER) Long ownerId) {
        log.info("GET-запрос к /items на получение всех вещей владельца с id={}", ownerId);
        return itemService.getItemsByOwner(ownerId);
    }

    @ResponseBody
    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestBody ItemDto itemDto, @PathVariable Long itemId,
                          @RequestHeader(OWNER) Long ownerId) {
        log.info("PATCH-запрос к /items на обновление вещи с id={}", itemId);
        ItemDto newItemDto = null;
        if (check.isExistUser(ownerId)) {
            newItemDto = itemService.update(itemDto, ownerId, itemId);
        }
        return newItemDto;
    }

    @DeleteMapping("/{itemId}")
    public ItemDto delete(@PathVariable Long itemId, @RequestHeader(OWNER) Long ownerId) {
        log.info("DELETE-запрос к /items на удаление вещи с id={}", itemId);
        return itemService.delete(itemId, ownerId);
    }

    @GetMapping("/search")
    public List<ItemDto> getItemsBySearchQuery(@RequestParam String text) {
        log.info("GET-запрос к /items/search на поиск вещи: {}", text);
        return itemService.getItemsBySearchQuery(text);
    }
}
