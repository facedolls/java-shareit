package ru.practicum.shareit.booking;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoCreate;
import ru.practicum.shareit.booking.dto.BookingDtoInfo;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        uses = {ItemMapper.class, UserMapper.class})
public interface BookingMapper {
    // check
    BookingDto toBookingDto(Booking booking);

    // check
    Collection<BookingDto> toBookingDto(Collection<Booking> booking);

    // check
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "booker", source = "user")
    @Mapping(target = "status", constant = "WAITING")
    Booking toBooking(BookingDtoCreate bookingDtoCreate, User user, Item item);

    // check
    @Mapping(target = "bookerId", source = "booker.id")
    @Mapping(target = "itemId", source = "item.id")
    BookingDtoInfo toBookingDtoInfo(Booking booking);

    // check
    List<BookingDtoInfo> toBookingDtoInfo(List<Booking> bookings);

    default Map<Long, BookingDtoInfo> toBookingDtoInfoMap(List<BookingDtoInfo> booking) {
        return booking.stream().collect(Collectors.toMap(
                BookingDtoInfo::getItemId, bookingDtoInfo -> bookingDtoInfo));
    }
}
