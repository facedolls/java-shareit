package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoCreate;

import java.util.Collection;

public interface BookingService {
    BookingDto createBooking(Long userId, BookingDtoCreate bookingDtoCreate);

    BookingDto updateBooking(Long userId, Long bookingId, Boolean approved);

    BookingDto getOneBookingUser(Long bookingId, Long userId);

    Collection<BookingDto> getAllBookingsBooker(Long userId, BookingState bookingState);

    Collection<BookingDto> getAllBookingsOwner(Long userId, BookingState bookingState);
}
