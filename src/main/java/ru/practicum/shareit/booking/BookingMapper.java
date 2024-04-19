package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;

public class BookingMapper {
    public static BookingDto toBookingDto(Booking booking) {
        return new BookingDto(
                booking.getItemId(),
                booking.getBookerId(),
                booking.getStatus(),
                booking.getStart(),
                booking.getEnd()
        );
    }
}
