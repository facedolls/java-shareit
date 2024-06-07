package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoCreate;

import java.util.Collection;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {
    private static final String USER_ID = "X-Sharer-User-Id";
    private static final String STATE = "ALL";
    private static final String PAGE_FROM = "0";
    private static final String PAGE_SIZE = "10";
    private final BookingService bookingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingDto createBooking(@RequestBody BookingDtoCreate bookingDtoCreate,
                                    @RequestHeader(USER_ID) Long userId) {
        log.info("POST user request id={} for create booking, request body={}", userId, bookingDtoCreate);
        return bookingService.createBooking(bookingDtoCreate, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto updateBooking(@RequestHeader(USER_ID) Long userId,
                                    @PathVariable Long bookingId,
                                    @RequestParam Boolean approved) {
        log.info("PATCH owner request id={} for update booking with id={}, parameter={}",
                userId, bookingId, approved);
        return bookingService.updateBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getOneBookingUser(@PathVariable Long bookingId,
                                        @RequestHeader(USER_ID) Long userId) {
        log.info("GET user request id={} for view booking with id={}", userId, bookingId);
        return bookingService.getOneBookingUser(bookingId, userId);
    }

    @GetMapping
    public Collection<BookingDto> getAllBookingsBooker(@RequestHeader(USER_ID) Long userId,
                                                       @RequestParam(defaultValue = STATE) String state,
                                                       @RequestParam(defaultValue = PAGE_FROM) Integer from,
                                                       @RequestParam(defaultValue = PAGE_SIZE) Integer size) {
        log.info("GET user request id={} for view bookings state={}. Page from={}, page size={}",
                userId, state, from, size);
        return bookingService.getAllBookingsBooker(userId, BookingState.valueOf(state), from, size);
    }

    @GetMapping("/owner")
    public Collection<BookingDto> getAllBookingsOwner(@RequestHeader(USER_ID) Long userId,
                                                      @RequestParam(defaultValue = STATE) String state,
                                                      @RequestParam(defaultValue = PAGE_FROM) Integer from,
                                                      @RequestParam(defaultValue = PAGE_SIZE) Integer size) {
        log.info("GET owner request id={} for view bookings state={}. Page from={}, page size={}",
                userId, state, from, size);
        return bookingService.getAllBookingsOwner(userId, BookingState.valueOf(state), from, size);
    }
}
