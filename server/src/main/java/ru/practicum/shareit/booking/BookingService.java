package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoCreate;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.Collection;

import static ru.practicum.shareit.booking.BookingStatus.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingService {
    private final BookingRepository bookingRepository;
    private final ItemService itemService;
    private final UserService userService;
    private final BookingMapper bookingMapper;

    @Transactional
    public BookingDto createBooking(BookingDtoCreate bookingDtoCreate, Long userId) {
        User booker = getUserIfTheExists(userId);
        Item item = getAvailableItemByIdIfItExists(bookingDtoCreate.getItemId(), userId);

        getExceptionIfUserIsNotBooker(userId, item);
        Booking booking = bookingRepository.save(bookingMapper.toBooking(bookingDtoCreate, booker, item));
        log.info("User id={} created booking id={} : {}", userId, booking.getId(), bookingDtoCreate);
        return bookingMapper.toBookingDto(booking);
    }

    @Transactional
    public BookingDto updateBooking(Long userId, Long bookingId, Boolean approved) {
        Booking bookingOld = getBookingNotWaitingIfItExists(userId, bookingId);
        BookingStatus status = approved ? APPROVED : REJECTED;
        bookingOld.setStatus(status);
        getExceptionIfUserIsNotOwner(userId, bookingOld);

        Booking bookingUpdated = bookingRepository.save(bookingOld);
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
    public Collection<BookingDto> getAllBookingsBooker(Long userId, BookingState bookingState,
                                                       Integer from, Integer size) {
        getUserIfTheExists(userId);
        Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Order.desc("start")));
        Collection<Booking> allBookings = getBookingsForBooker(bookingState, userId, pageable);
        log.info("Information about bookings was obtained by booker id={}", userId);
        return bookingMapper.toBookingDto(allBookings);
    }

    @Transactional(readOnly = true)
    public Collection<BookingDto> getAllBookingsOwner(Long userId, BookingState bookingState,
                                                      Integer from, Integer size) {
        getUserIfTheExists(userId);
        Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Order.desc("start")));
        Collection<Booking> allBookings = getBookingsForOwner(bookingState, userId, pageable);
        log.info("Information about bookings was obtained by owner id={}", userId);
        return bookingMapper.toBookingDto(allBookings);
    }

    private User getUserIfTheExists(Long userId) {
        return userService.findById(userId);
    }

    private Item getAvailableItemByIdIfItExists(Long itemId, Long userId) {
        Item item = itemService.findById(itemId);

        if (item.getAvailable().equals(false)) {
            log.warn("Item with id={} not found or not available", itemId);
            throw new ValidationException("Item with this id=" + itemId + " not found or not available");
        }
        return item;
    }

    private Booking getBookingNotWaitingIfItExists(Long userId, Long bookingId) {
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

    private void getExceptionIfUserIsNotOwner(Long userId, Booking booking) {
        if (!booking.getItem().getOwner().getId().equals(userId)) {
            log.warn("User id={} for booking id={} is not owner", userId, booking.getId());
            throw new NotFoundException("Booking id=" + booking.getId() + " not found");
        }
    }

    private void getExceptionIfUserIsNotBooker(Long userId, Item item) {
        if (item.getOwner().getId().equals(userId)) {
            log.warn("Owner id={} is trying to reserve his item id={}", userId, item.getOwner().getId());
            throw new NotFoundException("Owner cannot booking his item");
        }
    }

    private Collection<Booking> getBookingsForOwner(BookingState state, Long userId, Pageable pageable) {
        LocalDateTime current = LocalDateTime.now();
        switch (state) {
            case PAST:
                return bookingRepository.findAllByItem_Owner_IdAndEndBefore(userId, current, pageable);
            case FUTURE:
                return bookingRepository.findAllByItem_Owner_IdAndStartAfter(userId, current, pageable);
            case WAITING:
                return bookingRepository.findAllByItem_Owner_IdAndStatus(userId, WAITING, pageable);
            case REJECTED:
                return bookingRepository.findAllByItem_Owner_IdAndStatus(userId, REJECTED, pageable);
            case CURRENT:
                return bookingRepository
                        .findAllByItem_Owner_IdAndStartBeforeAndEndAfter(userId, current, current, pageable);
        }
        return bookingRepository.findAllByItem_Owner_Id(userId, pageable);
    }

    private Collection<Booking> getBookingsForBooker(BookingState state, Long userId, Pageable pageable) {
        LocalDateTime current = LocalDateTime.now();
        switch (state) {
            case PAST:
                return bookingRepository.findAllByBooker_IdAndEndBefore(userId, current, pageable);
            case FUTURE:
                return bookingRepository.findAllByBooker_IdAndStartAfter(userId, current, pageable);
            case WAITING:
                return bookingRepository.findAllByBooker_IdAndStatus(userId, WAITING, pageable);
            case REJECTED:
                return bookingRepository.findAllByBooker_IdAndStatus(userId, REJECTED, pageable);
            case CURRENT:
                return bookingRepository
                        .findAllByBooker_IdAndStartBeforeAndEndAfter(userId, current, current, pageable);
        }
        return bookingRepository.findAllByBooker_Id(userId, pageable);
    }
}
