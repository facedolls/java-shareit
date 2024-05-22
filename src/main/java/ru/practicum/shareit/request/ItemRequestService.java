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
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRequestMapper itemRequestMapper;

    public ItemRequestDtoInfo createItemRequest(ItemRequestDto itemRequestDto, Long userId) {
        User requester = getUserIfTheExists(userId);
        ItemRequest itemRequest = itemRequestMapper.toItemRequest(itemRequestDto, requester, LocalDateTime.now());
        ItemRequest createdItemRequest = itemRequestRepository.save(itemRequest);

        log.info("Request id={} created by user id={}", createdItemRequest.getId(), userId);
        return itemRequestMapper.toItemRequestDtoInfo(createdItemRequest);
    }

    public List<ItemRequestDtoInfo> getListOfRequestsForItemsUser(Long userId) {
        getUserIfTheExists(userId);
        List<ItemRequest> allRequestsUser = itemRequestRepository.findAllByRequester_IdOrderByCreatedDesc(userId);

        log.info("List of requests for items was received by a user with id={}", userId);
        return itemRequestMapper.toItemRequestDtoInfoList(allRequestsUser);
    }

    public List<ItemRequestDtoInfo> getItemRequestsPageByPage(Integer from, Integer size, Long userId) {
        getUserIfTheExists(userId);
        Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Order.desc("created")));
        List<ItemRequest> requests = itemRequestRepository.findAllByRequester_IdNot(userId, pageable);

        log.info("List of requests for items was received by a user with id={}", userId);
        return itemRequestMapper.toItemRequestDtoInfoList(requests);
    }

    public ItemRequestDtoInfo getItemRequestById(Long requestId, Long userId) {
        getUserIfTheExists(userId);
        ItemRequest itemRequest = itemRequestRepository.findById(requestId).orElseThrow(() -> {
            log.warn("Request id={} for user id={} not found", requestId, userId);
            throw new NotFoundException("Request id=" + requestId + " not found");
        });

        log.info("Request for items was received by a user with id={}", userId);
        return itemRequestMapper.toItemRequestDtoInfo(itemRequest);
    }

    private User getUserIfTheExists(Long userId) {
        return userRepository.findById(userId).stream().findFirst().orElseThrow(() -> {
            log.warn("User with id={} not found", userId);
            throw new NotFoundException("User with id=" + userId + " not found");
        });
    }
}
