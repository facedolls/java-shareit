package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoInfo;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.BookingStatus.APPROVED;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemMapper itemMapper;
    private final UserMapper userMapper;
    private final BookingMapper bookingMapper;
    private final CommentMapper commentMapper;

    @Transactional(readOnly = true)
    @Override
    public ItemDtoInfo getItemById(Long itemId, Long userId) {
        Item item = getById(itemId, userId);
        List<Comment> comments = commentRepository.findByItem_Id(itemId);
        Map<Long, List<CommentDto>> commentsItem = returnComments(comments);
        boolean isOwner = itemRepository.existsByIdAndOwner_Id(itemId, userId);
        if (!isOwner) {
            return itemMapper.toItemDtoInfo(item, commentsItem);
        }
        return setBookingsForOwner(List.of(item), List.of(itemId), commentsItem).stream().findFirst().orElse(null);
    }

    @Transactional(readOnly = true)
    @Override
    public Item getItemByIdAvailable(Long itemId, Long userId) {
        Item item = getById(itemId, userId);
        if (item.getAvailable().equals(false)) {
            log.warn("Item with id={} not found or not available", itemId);
            throw new ValidationException("Item with this id=" + itemId + " not found or not available");
        }
        return item;
    }

    @Transactional(readOnly = true)
    @Override
    public Collection<ItemDtoInfo> getAllItemUser(Long userId) {
        List<Item> items = itemRepository.findAllByOwnerId(userId);
        List<Long> itemsId = items.stream().map(Item::getId).collect(Collectors.toList());
        List<Comment> comments = commentRepository.findAllByItem_IdIn(itemsId);
        Map<Long, List<CommentDto>> commentsItems = returnComments(comments);
        log.info("All items have been received");
        return setBookingsForOwner(items, itemsId, commentsItems);
    }

    @Transactional
    @Override
    public ItemDto createItem(Long userId, ItemDto itemDto) {
        User user = userMapper.toUser(userService.getUserById(userId));
        Item item = itemRepository.save(itemMapper.toItem(itemDto, user));
        log.info("Item has been created={}", item);
        return itemMapper.toItemDto(item);
    }

    @Transactional
    @Override
    public ItemDto updateItem(Long userId, Long itemId, ItemDto itemDtoNew) {
        User user = userMapper.toUser(userService.getUserById(userId));
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
    @Override
    public Collection<ItemDto> searchItems(String text) {
        if (text.isEmpty()) {
            return new ArrayList<>();
        }
        Collection<Item> items = itemRepository.searchItems(text);
        log.info("Items={} by text={} received", items, text);
        return itemMapper.toItemDto(items);
    }

    @Override
    public CommentDto createComment(CommentDto commentDto, Long userId, Long itemId) {
        User user = userService.getById(userId);
        Item item = itemRepository.findById(itemId).orElseThrow(() -> {
            log.warn("A user={} wants to leave a review for an item id={} that doesn't exist", userId, itemId);
            throw new ValidationException("Item doesn't exist yet");
        });
        isBookerOfThisItem(userId, itemId);
        commentDto.setCreated(LocalDateTime.now());

        Comment comment = commentMapper.toComment(commentDto, user, item);
        comment = commentRepository.save(comment);
        return commentMapper.toCommentDto(comment);
    }

    private void isBookerOfThisItem(Long userId, Long itemId) {
        boolean isValid = bookingRepository.existsByItemIdAndBookerIdAndStatusAndEndBefore(
                itemId, userId, APPROVED, LocalDateTime.now());
        if (!isValid) {
            throw new ValidationException("Only users whose booking has expired can leave comments");
        }
    }

    private Item getById(Long itemId, Long userId) {
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
        List<Booking> nextBookings = bookingRepository.findNextBookingsForOwner(current, itemsId, APPROVED);
        List<Booking> lastBookings = bookingRepository.findLastBookingsForOwner(current, itemsId, APPROVED);
        return bookingMapper.toItemDtoForOwner(items, nextBookings, lastBookings, commentsItem);
    }

    private Map<Long, List<CommentDto>> returnComments(List<Comment> comments) {
        List<CommentDto> allCommentDtoItems = commentMapper.toCommentDto(comments);
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
}
