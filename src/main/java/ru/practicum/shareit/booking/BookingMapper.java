package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;
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

import static ru.practicum.shareit.booking.BookingStatus.WAITING;

@Component
@RequiredArgsConstructor
public class BookingMapper {
    private final ItemMapper itemMapper;
    private final UserMapper userMapper;

    public BookingDto toBookingDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(itemMapper.toItemDto(booking.getItem()))
                .booker(userMapper.toUserDto(booking.getBooker()))
                .status(booking.getStatus())
                .build();
    }

    public Collection<BookingDto> toBookingDtoCollection(Collection<Booking> booking) {
        return booking.stream()
                .map(this::toBookingDto)
                .collect(Collectors.toList());
    }

    public Booking toBooking(BookingDtoCreate bookingDtoCreate, User user, Item item) {
        return Booking.builder()
                .start(bookingDtoCreate.getStart())
                .end(bookingDtoCreate.getEnd())
                .item(item)
                .booker(user)
                .status(WAITING)
                .build();
    }

    public BookingDtoInfo toBookingDtoInfo(Booking booking) {
        return BookingDtoInfo.builder()
                .id(booking.getId())
                .bookerId(booking.getBooker().getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .itemId(booking.getItem().getId())
                .build();
    }

    public List<BookingDtoInfo> toBookingDtoInfoList(List<Booking> bookings) {
        return bookings.stream()
                .map(this::toBookingDtoInfo)
                .collect(Collectors.toList());
    }

    public Map<Long, BookingDtoInfo> toBookingDtoInfoMapByIdItem(List<BookingDtoInfo> booking) {
        return booking.stream().collect(Collectors.toMap(
                BookingDtoInfo::getItemId, bookingDtoInfo -> bookingDtoInfo));
    }
}

//@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
//public interface BookingMapper {
//    BookingDto toBookingDto(Booking booking);
//
//    Collection<BookingDto> toBookingDtoCollection(Collection<Booking> booking);
//
//    @Mapping(target = "id", source = "bookingDtoCreate.itemId")
//    @Mapping(target = "booker", source = "user")
//    @Mapping(target = "status", source = WAITING)
//    Booking toBooking(BookingDtoCreate bookingDtoCreate, User user, Item item);
//
//    BookingDtoInfo toBookingDtoInfo(Booking booking);
//
//    List<BookingDtoInfo> toBookingDtoInfoList(List<Booking> bookings);
//
//    Map<Long, BookingDtoInfo> toBookingDtoInfoMapByIdItem(List<BookingDtoInfo> booking);
//}
