package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingItemService;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingDtoInfo;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoInfo;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.BookingStatus.APPROVED;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemService {
    public static final String NEXT = "next";
    public static final String LAST = "last";
    private final ItemRepository itemRepository;
    private final CommentService commentService;
    private final BookingItemService bookingItemService;
    private final ItemRequestService itemRequestService;
    private final UserService userService;
    private final ItemMapper itemMapper;
    private final BookingMapper bookingMapper;
    private final CommentMapper commentMapper;

    @Transactional(readOnly = true)
    public ItemDtoInfo getItemDtoById(Long itemId, Long userId) {
        Item item = getItemById(itemId, userId);
        List<Comment> comments = commentService.getAllByItem_Id(itemId);
        Map<Long, List<CommentDto>> commentsItem = getCommentDtoSortByIdItem(comments);

        boolean isOwner = itemRepository.existsByIdAndOwner_Id(itemId, userId);
        if (!isOwner) {
            List<CommentDto> commentDto = commentsItem.isEmpty() ? new ArrayList<>() : commentsItem.get(item.getId());
            return itemMapper.toOneItemDtoInfoForAllUsers(item, commentDto);
        }

        log.info("Information aboutitem id={} was obtained byuser id={}", itemId, userId);
        return setBookingsForOwner(List.of(item), List.of(itemId), commentsItem).stream().findFirst().orElse(null);
    }

    @Transactional(readOnly = true)
    public Collection<ItemDtoInfo> getAllItemUser(Long userId, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Order.asc("id")));
        List<Item> items = itemRepository.findAllByOwnerId(userId, pageable);
        List<Long> itemsId = items.stream().map(Item::getId).collect(Collectors.toList());

        List<Comment> comments = commentService.getCommentsByItemIdIn(itemsId);
        Map<Long, List<CommentDto>> commentsItems = getCommentDtoSortByIdItem(comments);

        log.info("All items have been received");
        return setBookingsForOwner(items, itemsId, commentsItems);
    }

    @Transactional
    public ItemDto createItem(ItemDto itemDto, Long userId) {
        User user = getUserIfTheExists(userId);
        Long requestId = itemDto.getRequestId();
        ItemRequest itemRequest = requestId == null ? null : itemRequestService.findById(requestId);
        Item item = itemRepository.save(itemMapper.toItem(itemDto, user, itemRequest));
        log.info("Item has been created={}", item);
        return itemMapper.toItemDto(item);
    }

    @Transactional
    public ItemDto updateItem(ItemDto itemDtoNew, Long itemId, Long userId) {
        User user = getUserIfTheExists(userId);
        Item itemOld = itemRepository.findById(itemId).stream().findFirst().orElse(null);
        if (itemOld == null || !itemOld.getOwner().getId().equals(userId)) {
            log.warn("Item with this id={} not found", itemId);
            throw new NotFoundException("Item with this id=" + itemId + " not found");
        }

        setItemDto(itemOld, itemDtoNew, user);
        Item item = itemRepository.save(itemOld);
        log.info("Item has been updated={}", item);
        return itemMapper.toItemDto(item);
    }

    @Transactional(readOnly = true)
    public Collection<ItemDto> searchItems(String text, Integer from, Integer size) {
        if (text.isEmpty()) {
            return new ArrayList<>();
        }
        Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Order.asc("id")));
        Collection<Item> items = itemRepository
                .findByAvailableTrueAndDescriptionContainsIgnoreCaseOrAvailableTrueAndNameContainsIgnoreCase(
                        text, text, pageable);
        log.info("Items={} by text={} received", items, text);
        return itemMapper.toItemDtoCollection(items);
    }

    public Item findById(Long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> {
            log.warn("Item with this id={} not found for user", itemId);
            throw new NotFoundException("Item with this id=" + itemId + " not found");
        });
        return item;
    }

    public CommentDto createComment(CommentDto commentDto, Long userId, Long itemId) {
        User user = getUserIfTheExists(userId);
        Item item = itemRepository.findById(itemId).orElseThrow(() -> {
            log.warn("User={} try to leave review for item id={} that doesn't exist", userId, itemId);
            throw new ValidationException("Item doesn't exist yet");
        });
        getExceptionIfIsNotBookerOfThisItem(userId, itemId);
        commentDto.setCreated(LocalDateTime.now());

        Comment comment = commentMapper.toComment(commentDto, user, item);
        Comment commentSaved = commentService.saveComment(comment);
        log.info("Created comment id={} about item={} by user id={}", commentSaved.getId(), itemId, userId);
        return commentMapper.toCommentDto(commentSaved);
    }

    private Item getItemById(Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> {
            log.warn("Item with this id={} not found for user id={}", itemId, userId);
            throw new NotFoundException("Item with this id=" + itemId + " not found");
        });

        log.info("Item with id={} was received user with id={}", itemId, userId);
        return item;
    }

    private void setItemDto(Item itemOld, ItemDto itemDtoNew, User owner) {
        if (itemDtoNew.getName() != null && !itemDtoNew.getName().isEmpty()) {
            itemOld.setName(itemDtoNew.getName());
        }
        if (itemDtoNew.getDescription() != null && !itemDtoNew.getDescription().isEmpty()) {
            itemOld.setDescription(itemDtoNew.getDescription());
        }
        if (itemDtoNew.getAvailable() != null) {
            itemOld.setAvailable(itemDtoNew.getAvailable());
        }
        itemOld.setOwner(owner);
    }

    private Collection<ItemDtoInfo> setBookingsForOwner(List<Item> items, List<Long> itemsId,
                                                        Map<Long, List<CommentDto>> commentsItem) {
        LocalDateTime current = LocalDateTime.now();
        List<Booking> nextBookings = bookingItemService.getNextBookingsForOwner(current, itemsId, APPROVED);
        List<Booking> lastBookings = bookingItemService.getLastBookingsForOwner(current, itemsId, APPROVED);
        return getItemDtoInfoForOwner(items, nextBookings, lastBookings, commentsItem);
    }

    private Collection<ItemDtoInfo> getItemDtoInfoForOwner(List<Item> items, List<Booking> next, List<Booking> last,
                                                           Map<Long, List<CommentDto>> commentsItem) {
        Map<String, Map<Long, BookingDtoInfo>> booking = getBookingDtoInfoMapByNextAndLast(next, last);
        Map<Long, BookingDtoInfo> nextBooking = booking.get(NEXT);
        Map<Long, BookingDtoInfo> lastBooking = booking.get(LAST);
        return items.stream()
                .map(item -> {
                    BookingDtoInfo nextDto = null;
                    BookingDtoInfo lastDto = null;
                    List<CommentDto> commentDto = new ArrayList<>();
                    if (nextBooking != null && nextBooking.containsKey(item.getId())) {
                        nextDto = nextBooking.get(item.getId());
                    }
                    if (lastBooking != null && lastBooking.containsKey(item.getId())) {
                        lastDto = lastBooking.get(item.getId());
                    }
                    if (commentsItem.containsKey(item.getId())) {
                        commentDto = commentsItem.get(item.getId());
                    }
                    return itemMapper.toOneItemDtoInfoForOwner(item, nextDto, lastDto, commentDto);
                }).sorted(Comparator.comparing(ItemDtoInfo::getId))
                .collect(Collectors.toList());
    }

    private Map<String, Map<Long, BookingDtoInfo>> getBookingDtoInfoMapByNextAndLast(List<Booking> nextBookings,
                                                                                     List<Booking> lastBookings) {
        List<BookingDtoInfo> nextBookingDtoInfo = bookingMapper.toBookingDtoInfo(nextBookings);
        List<BookingDtoInfo> lastBookingDtoInfo = bookingMapper.toBookingDtoInfo(lastBookings);

        Map<Long, BookingDtoInfo> next = bookingMapper.toBookingDtoInfoMap(nextBookingDtoInfo);
        Map<Long, BookingDtoInfo> last = bookingMapper.toBookingDtoInfoMap(lastBookingDtoInfo);
        Map<String, Map<Long, BookingDtoInfo>> result = new HashMap<>();

        if (!next.isEmpty()) {
            result.put(NEXT, next);
        }
        if (!last.isEmpty()) {
            result.put(LAST, last);
        }
        return result;
    }

    private Map<Long, List<CommentDto>> getCommentDtoSortByIdItem(List<Comment> comments) {
        List<CommentDto> allCommentDtoItems = commentMapper.toCommentDtoList(comments);
        Map<Long, List<CommentDto>> result = new HashMap<>();
        allCommentDtoItems.forEach(commentDto -> {
            List<CommentDto> comment = new ArrayList<>();
            if (result.containsKey(commentDto.getItemId())) {
                comment = result.get(commentDto.getItemId());
                comment.add(commentDto);
            } else {
                comment.add(commentDto);
            }
            result.put(commentDto.getItemId(), comment);
        });
        return result;
    }

    private User getUserIfTheExists(Long userId) {
        return userService.findById(userId);
    }

    private void getExceptionIfIsNotBookerOfThisItem(Long userId, Long itemId) {
        boolean isValid = bookingItemService.isExistsByItemIdAndBookerIdAndStatusAndEndBefore(
                itemId, userId, APPROVED, LocalDateTime.now());
        if (!isValid) {
            throw new ValidationException("Only users whose booking has expired can leave comments");
        }
    }
}
