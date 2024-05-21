package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoInfo;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemRequestDtoInfo createItemRequest(@Valid @RequestBody ItemRequestDto itemRequestDto,
                                                @RequestHeader("X-Sharer-User-Id") @Positive Long userId) {
        return itemRequestService.createItemRequest(itemRequestDto, userId);
    }

    @GetMapping
    public List<ItemRequestDtoInfo> getListOfRequestsForItemsUser(@RequestHeader("X-Sharer-User-Id")
                                                                  @Positive Long userId) {
        return itemRequestService.getListOfRequestsForItemsUser(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDtoInfo> getItemRequestsPageByPage(@RequestParam(defaultValue = "0", required = false)
                                                              @Min(0) Integer from,
                                                              @RequestParam(defaultValue = "10", required = false)
                                                              @Min(1) Integer size,
                                                              @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestService.getItemRequestsPageByPage(from, size, userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDtoInfo getItemRequestById(@PathVariable @Positive Long requestId,
                                                 @RequestHeader("X-Sharer-User-Id") @Positive Long userId) {
        return itemRequestService.getItemRequestById(requestId, userId);
    }
}
