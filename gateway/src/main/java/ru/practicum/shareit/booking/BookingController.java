package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoCreate;
import ru.practicum.shareit.booking.dto.validator.ValidState;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {
    private static final String USER_ID = "X-Sharer-User-Id";
    private final BookingClient bookingClient;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> createBooking(@Valid @RequestBody BookingDtoCreate bookingDtoCreate,
                                                @RequestHeader(USER_ID) Long userId) {
        return bookingClient.createBooking(bookingDtoCreate, userId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> updateBooking(@RequestHeader(USER_ID) Long userId,
                                                @PathVariable @Positive @NotNull Long bookingId,
                                                @RequestParam @NotNull Boolean approved) {
        return bookingClient.updateBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getOneBookingUser(@PathVariable @Positive @NotNull Long bookingId,
                                                    @RequestHeader(USER_ID) Long userId) {
        return bookingClient.getOneBookingUser(bookingId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllBookingsBooker(@RequestHeader(USER_ID) Long userId,
                                                       @RequestParam(defaultValue = "ALL") @ValidState String state,
                                                       @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                                       @RequestParam(defaultValue = "10") @Min(1) Integer size) {
        return bookingClient.getAllBookingsBooker(userId, BookingState.valueOf(state), from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllBookingsOwner(@RequestHeader(USER_ID) Long userId,
                                                      @RequestParam(defaultValue = "ALL") @ValidState String state,
                                                      @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                                      @RequestParam(defaultValue = "10") @Min(1) Integer size) {
        return bookingClient.getAllBookingsOwner(userId, BookingState.valueOf(state), from, size);
    }
}
