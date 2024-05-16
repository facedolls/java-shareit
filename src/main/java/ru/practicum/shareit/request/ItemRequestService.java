package ru.practicum.shareit.request;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ItemRequestService {
    ItemRequestDto create(ItemRequestDto itemRequestDto, Long requestorId, LocalDateTime created) {
        return null;
    };

    ItemRequestDto getItemRequestById(Long itemRequestId, Long userId) {
        return null;
    };

    List<ItemRequestDto> getOwnItemRequests(Long requesterId) {
        return null;
    };

    List<ItemRequestDto> getAllItemRequests(Long userId, Integer from, Integer size) {
        return null;
    };
}
