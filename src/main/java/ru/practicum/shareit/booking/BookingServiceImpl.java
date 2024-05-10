package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoCreate;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.Collection;

import static ru.practicum.shareit.booking.BookingStatus.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemService itemService;
    private final BookingMapper bookingMapper;

    @Transactional
    @Override
    public BookingDto createBooking(Long userId, BookingDtoCreate bookingDtoCreate) {
        User booker = userService.getById(userId);
        Item item = itemService.getItemByIdAvailable(bookingDtoCreate.getItemId(), userId);
        isBooker(userId, item);
        Booking booking = bookingRepository.save(bookingMapper.toBooking(bookingDtoCreate, booker, item));
        log.info("User id={} created booking id={} : {}", userId, booking.getId(), bookingDtoCreate);
        return bookingMapper.toBookingDto(booking);
    }

    @Transactional
    @Override
    public BookingDto updateBooking(Long userId, Long bookingId, Boolean approved) {
        Booking bookingOld = isBooking(userId, bookingId);
        BookingStatus status = approved ? APPROVED : REJECTED;
        bookingOld.setStatus(status);
        isOwner(userId, bookingOld);
        Booking bookingUpdated = bookingRepository.save(bookingOld);
        return bookingMapper.toBookingDto(bookingUpdated);
    }

    @Transactional(readOnly = true)
    @Override
    public BookingDto getOneBookingUser(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findBookingByIdAndUser(bookingId, userId).orElseThrow(() -> {
            log.warn("Booking with this id={} not found", bookingId);
            throw new NotFoundException("Booking with this id=" + bookingId + " not found");
        });
        return bookingMapper.toBookingDto(booking);
    }

    @Transactional(readOnly = true)
    @Override
    public Collection<BookingDto> getAllBookingsBooker(Long userId, BookingState bookingState) {
        userService.getById(userId);
        Collection<Booking> allBookings = getBookingsForBooker(bookingState, userId);
        return bookingMapper.toBookingDto(allBookings);
    }

    @Transactional(readOnly = true)
    @Override
    public Collection<BookingDto> getAllBookingsOwner(Long userId, BookingState bookingState) {
        userService.getById(userId);
        Collection<Booking> allBookings = getBookingsForOwner(bookingState, userId);
        return bookingMapper.toBookingDto(allBookings);
    }

    private Booking isBooking(Long userId, Long bookingId) {
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
            log.warn("User id={} for booking id={} is not the owner", userId, booking.getId());
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
            case CURRENT:
                return bookingRepository.findAllByStartAndEndAndOwner(current, userId);
            case PAST:
                return bookingRepository.findAllByEndBeforeAndOwner(current, userId);
            case FUTURE:
                return bookingRepository.findAllByStartAfterAndOwner(current, userId);
            case WAITING:
                return bookingRepository.findAllBookingByStatusAndByOwner(WAITING, userId);
            case REJECTED:
                return bookingRepository.findAllBookingByStatusAndByOwner(REJECTED, userId);
        }
        return bookingRepository.findAllBookingByOwner(userId);
    }

    private Collection<Booking> getBookingsForBooker(BookingState state, Long userId) {
        LocalDateTime current = LocalDateTime.now();
        switch (state) {
            case CURRENT:
                return bookingRepository.findAllByStartAndEndAndBooker(current, userId);
            case PAST:
                return bookingRepository.findAllByEndBeforeAndBooker(current, userId);
            case FUTURE:
                return bookingRepository.findAllByStartAfterAndBooker(current, userId);
            case WAITING:
                return bookingRepository.findAllBookingByStatusAndByBooker(WAITING, userId);
            case REJECTED:
                return bookingRepository.findAllBookingByStatusAndByBooker(REJECTED, userId);
        }
        return bookingRepository.findAllBookingByBooker(userId);
    }
}
