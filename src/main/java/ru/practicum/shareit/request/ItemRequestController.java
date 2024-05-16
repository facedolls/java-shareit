package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {
    private static final String USER_ID = "X-Sharer-User-Id";
    private final ItemRequestService itemRequestService;

    @ResponseBody
    @PostMapping
    public ItemRequestDto create(@RequestBody ItemRequestDto itemRequestDto, @RequestHeader(USER_ID) Long requesterId) {
        log.info("Create request: {} with user id=", requesterId);
        return itemRequestService.create(itemRequestDto, requesterId, LocalDateTime.now());
    };

    @GetMapping("/{requestId}")
    public ItemRequestDto getItemRequestById(@PathVariable("request_id") Long itemRequestId, @RequestHeader(USER_ID) Long userId) {
        return itemRequestService.getItemRequestById(itemRequestId, userId);
    };

    @GetMapping
    public List<ItemRequestDto> getItems(@RequestHeader(USER_ID) Long userId) {
        return itemRequestService.getOwnItemRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllItemRequests(@RequestHeader(USER_ID) Long requesterId) {
        return null;
    }
}
