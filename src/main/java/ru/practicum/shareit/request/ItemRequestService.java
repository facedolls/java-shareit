package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoInfo;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserService userService;
    private final ItemRequestMapper itemRequestMapper;
    private final UserMapper userMapper;

    public ItemRequestDtoInfo createItemRequest(ItemRequestDto itemRequestDto, Long userId) {
        User requester = userMapper.toUser(userService.getUserById(userId));
        ItemRequest itemRequest = itemRequestMapper.toItemRequest(itemRequestDto, requester, LocalDateTime.now());
        ItemRequest createdItemRequest = itemRequestRepository.save(itemRequest);

        log.info("Request id={} created by user id={}", createdItemRequest.getId(), userId);
        return itemRequestMapper.toItemRequestDtoInfo(createdItemRequest);
    }

    public List<ItemRequestDtoInfo> getListOfRequestsForItemsUser(Long userId) {
        userMapper.toUser(userService.getUserById(userId));
        List<ItemRequest> allRequestsUser = itemRequestRepository.findAllByRequester_IdOrderByCreatedDesc(userId);

        log.info("List of requests for items was received by a user with id={}", userId);
        return itemRequestMapper.toItemRequestDtoInfoList(allRequestsUser);
    }

    public List<ItemRequestDtoInfo> getItemRequestsPageByPage(Integer from, Integer size, Long userId) {
        userMapper.toUser(userService.getUserById(userId));
        Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Order.desc("created")));
        List<ItemRequest> requests = itemRequestRepository.findAllByRequester_IdNot(userId, pageable);

        log.info("List of requests for items was received by a user with id={}", userId);
        return itemRequestMapper.toItemRequestDtoInfoList(requests);
    }

    public ItemRequestDtoInfo getItemRequestById(Long requestId, Long userId) {
        userMapper.toUser(userService.getUserById(userId));
        ItemRequest itemRequest = itemRequestRepository.findById(requestId).orElseThrow(() -> {
            log.warn("Request id={} for user id={} not found", requestId, userId);
            throw new NotFoundException("Request id=" + requestId + " not found");
        });

        log.info("Request for items was received by a user with id={}", userId);
        return itemRequestMapper.toItemRequestDtoInfo(itemRequest);
    }
}
