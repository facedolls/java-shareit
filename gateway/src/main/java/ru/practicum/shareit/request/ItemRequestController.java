package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {
    public static final String USER_ID = "X-Sharer-User-Id";
    public static final String PAGE_FROM = "0";
    public static final String PAGE_SIZE = "10";
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> createItemRequest(@Valid @RequestBody ItemRequestDto itemRequestDto,
                                                    @RequestHeader(USER_ID) @Positive Long userId) {
        return itemRequestClient.createItemRequest(itemRequestDto, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getListOfRequestsForItemsUser(@RequestHeader(USER_ID) @Positive Long userId) {
        return itemRequestClient.getListOfRequestsForItemsUser(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getItemRequestsPageByPage(@RequestParam(defaultValue = PAGE_FROM)
                                                            @Min(0) Integer from,
                                                            @RequestParam(defaultValue = PAGE_SIZE)
                                                            @Min(1) Integer size,
                                                            @RequestHeader(USER_ID) Long userId) {
        return itemRequestClient.getItemRequestsPageByPage(from, size, userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequestById(@PathVariable @Positive Long requestId,
                                                     @RequestHeader(USER_ID) @Positive Long userId) {
        return itemRequestClient.getItemRequestById(requestId, userId);
    }
}
