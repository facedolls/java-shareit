package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoCreate;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.Collection;

import static ru.practicum.shareit.booking.BookingStatus.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingService {
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemService itemService;
    private final BookingMapper bookingMapper;
    private final UserMapper userMapper;
    private final ItemRepository itemRepository;

    @Transactional
    public BookingDto createBooking(BookingDtoCreate bookingDtoCreate, Long userId) {
        var booker = userMapper.toUser(userService.getUserById(userId));
        var item = isItemByIdAvailable(bookingDtoCreate.getItemId(), userId);

        isBooker(userId, item);
        Booking booking = bookingRepository.save(bookingMapper.toBooking(bookingDtoCreate, booker, item));
        log.info("User id={} created booking id={} : {}", userId, booking.getId(), bookingDtoCreate);
        return bookingMapper.toBookingDto(booking);
    }

    @Transactional
    public BookingDto updateBooking(Long userId, Long bookingId, Boolean approved) {
        var bookingOld = isBookingExistAndNotWaiting(userId, bookingId);
        var status = approved ? APPROVED : REJECTED;
        bookingOld.setStatus(status);
        isOwner(userId, bookingOld);
        var bookingUpdated = bookingRepository.save(bookingOld);
        log.info("Owner item updated status booking id={} to : {}", userId, status);
        return bookingMapper.toBookingDto(bookingUpdated);
    }

    @Transactional(readOnly = true)
    public BookingDto getOneBookingUser(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findBookingByIdAndUser(bookingId, userId).orElseThrow(() -> {
            log.warn("Booking with this id={} not found", bookingId);
            throw new NotFoundException("Booking with this id=" + bookingId + " not found");
        });
        log.info("Information about booking id={} was obtained by user id={}", bookingId, userId);
        return bookingMapper.toBookingDto(booking);
    }

    @Transactional(readOnly = true)
    public Collection<BookingDto> getAllBookingsBooker(Long userId, BookingState bookingState) {
        userService.getUserById(userId);
        Collection<Booking> allBookings = getBookingsForBooker(bookingState, userId);
        log.info("Information about bookings was obtained by booker id={}", userId);
        return bookingMapper.toBookingDto(allBookings);
    }

    @Transactional(readOnly = true)
    public Collection<BookingDto> getAllBookingsOwner(Long userId, BookingState bookingState) {
        userService.getUserById(userId);
        Collection<Booking> allBookings = getBookingsForOwner(bookingState, userId);
        log.info("Information about bookings was obtained by owner id={}", userId);
        return bookingMapper.toBookingDto(allBookings);
    }

    private Booking isBookingExistAndNotWaiting(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> {
            log.warn("Booking id={} user id={} not found", bookingId, userId);
            return new NotFoundException("Booking with id=" + bookingId + " not found");
        });

        if (!booking.getStatus().equals(WAITING)) {
            log.warn("Booking id={} status is not WAITING. Booking status is = {}", bookingId, booking.getStatus());
            throw new ValidationException("Booking status is not WAITING");
        }
        return booking;
    }

    private void isOwner(Long userId, Booking booking) {
        if (!booking.getItem().getOwner().getId().equals(userId)) {
            log.warn("User id={} for booking id={} is not owner", userId, booking.getId());
            throw new NotFoundException("Booking id=" + booking.getId() + " not found");
        }
    }

    private void isBooker(Long userId, Item item) {
        if (item.getOwner().getId().equals(userId)) {
            log.warn("Owner id={} is trying to reserve his item id={}", userId, item.getOwner().getId());
            throw new NotFoundException("Owner cannot booking his item");
        }
    }

    private Collection<Booking> getBookingsForOwner(BookingState state, Long userId) {
        LocalDateTime current = LocalDateTime.now();
        switch (state) {
            case PAST:
                return bookingRepository.findAllByItem_Owner_IdAndEndBeforeOrderByStartDesc(userId, current);
            case FUTURE:
                return bookingRepository.findAllByItem_Owner_IdAndStartAfterOrderByStartDesc(userId, current);
            case WAITING:
                return bookingRepository.findAllByItem_Owner_IdAndStatusOrderByStartDesc(userId, WAITING);
            case REJECTED:
                return bookingRepository.findAllByItem_Owner_IdAndStatusOrderByStartDesc(userId, REJECTED);
            case CURRENT:
                return bookingRepository
                        .findAllByItem_Owner_IdAndStartBeforeAndEndAfterOrderByStartDesc(userId, current, current);
        }
        return bookingRepository.findAllByItem_Owner_IdOrderByStartDesc(userId);
    }

    private Collection<Booking> getBookingsForBooker(BookingState state, Long userId) {
        LocalDateTime current = LocalDateTime.now();
        switch (state) {
            case PAST:
                return bookingRepository.findAllByBooker_IdAndEndBeforeOrderByStartDesc(userId, current);
            case FUTURE:
                return bookingRepository.findAllByBooker_IdAndStartAfterOrderByStartDesc(userId, current);
            case WAITING:
                return bookingRepository.findAllByBooker_IdAndStatusOrderByStartDesc(userId, WAITING);
            case REJECTED:
                return bookingRepository.findAllByBooker_IdAndStatusOrderByStartDesc(userId, REJECTED);
            case CURRENT:
                return bookingRepository
                        .findAllByBooker_IdAndStartBeforeAndEndAfterOrderByStartDesc(userId, current, current);
        }
        return bookingRepository.findAllByBooker_IdOrderByStartDesc(userId);
    }

    private Item isItemByIdAvailable(Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> {
            log.warn("Item with this id={} not found for user id={}", itemId, userId);
            throw new NotFoundException("Item with this id=" + itemId + " not found");
        });

        if (item.getAvailable().equals(false)) {
            log.warn("Item with id={} not found or not available", itemId);
            throw new ValidationException("Item with this id=" + itemId + " not found or not available");
        }
        return item;
    }
}
