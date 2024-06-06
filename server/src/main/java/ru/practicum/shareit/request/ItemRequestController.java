package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoInfo;

import java.util.List;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private static final String USER_ID = "X-Sharer-User-Id";
    private static final String PAGE_FROM = "0";
    private static final String PAGE_SIZE = "10";
    private final ItemRequestService itemRequestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemRequestDtoInfo createItemRequest(@RequestBody ItemRequestDto itemRequestDto,
                                                @RequestHeader(USER_ID) Long userId) {
        return itemRequestService.createItemRequest(itemRequestDto, userId);
    }

    @GetMapping
    public List<ItemRequestDtoInfo> getListOfRequestsForItemsUser(@RequestHeader(USER_ID) Long userId) {
        return itemRequestService.getListOfRequestsForItemsUser(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDtoInfo> getItemRequestsPageByPage(@RequestParam(defaultValue = PAGE_FROM)
                                                              Integer from,
                                                              @RequestParam(defaultValue = PAGE_SIZE)
                                                              Integer size,
                                                              @RequestHeader(USER_ID) Long userId) {
        return itemRequestService.getItemRequestsPageByPage(from, size, userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDtoInfo getItemRequestById(@PathVariable Long requestId,
                                                 @RequestHeader(USER_ID) Long userId) {
        return itemRequestService.getItemRequestById(requestId, userId);
    }
}
