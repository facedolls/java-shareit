package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoCreate;
import ru.practicum.shareit.booking.dto.validator.ValidState;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.Collection;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {
    private static final String USER_ID = "X-Sharer-User-Id";
    private final BookingService bookingService;

    @PostMapping
    public BookingDto createBooking(@Valid @RequestBody BookingDtoCreate bookingDtoCreate,
                                    @RequestHeader(USER_ID) Long userId) {
        return bookingService.createBooking(userId, bookingDtoCreate);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto updateBooking(@RequestHeader(USER_ID) Long userId,
                                    @PathVariable @Positive @NotNull Long bookingId,
                                    @RequestParam @NotNull Boolean approved) {
        return bookingService.updateBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getOneBookingUser(@PathVariable @Positive @NotNull Long bookingId,
                                        @RequestHeader(USER_ID) Long userId) {
        return bookingService.getOneBookingUser(bookingId, userId);
    }

    @GetMapping
    public Collection<BookingDto> getAllBookingsBooker(@RequestHeader(USER_ID) Long userId,
                                                       @RequestParam(defaultValue = "ALL")
                                                       @ValidState String state) {
        return bookingService.getAllBookingsBooker(userId, BookingState.valueOf(state));
    }

    @GetMapping("/owner")
    public Collection<BookingDto> getAllBookingsOwner(@RequestHeader(USER_ID) Long userId,
                                                      @RequestParam(defaultValue = "ALL")
                                                      @ValidState String state) {
        return bookingService.getAllBookingsOwner(userId, BookingState.valueOf(state));
    }
}
