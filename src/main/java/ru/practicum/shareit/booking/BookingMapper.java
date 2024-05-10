package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoCreate;
import ru.practicum.shareit.booking.dto.BookingDtoInfo;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDtoInfo;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;

import java.util.*;
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

    public Collection<BookingDto> toBookingDto(Collection<Booking> booking) {
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

    public List<BookingDtoInfo> toBookingDtoInfo(List<Booking> bookings) {
        return bookings.stream()
                .map(this::toBookingDtoInfo)
                .collect(Collectors.toList());
    }

    public Map<Long, BookingDtoInfo> toBookingDtoInfoMap(List<BookingDtoInfo> booking) {
        return booking.stream().collect(Collectors.toMap(
                BookingDtoInfo::getItemId, bookingDtoInfo -> bookingDtoInfo));
    }

    public Map<String, Map<Long, BookingDtoInfo>> toBookingDtoInfo(List<Booking> nextBookings,
                                                                   List<Booking> lastBookings) {
        List<BookingDtoInfo> nextBookingDtoInfo = toBookingDtoInfo(nextBookings);
        List<BookingDtoInfo> lastBookingDtoInfo = toBookingDtoInfo(lastBookings);

        Map<Long, BookingDtoInfo> next = toBookingDtoInfoMap(nextBookingDtoInfo);
        Map<Long, BookingDtoInfo> last = toBookingDtoInfoMap(lastBookingDtoInfo);
        Map<String, Map<Long, BookingDtoInfo>> result = new HashMap<>();

        if (!next.isEmpty()) {
            result.put("next", next);
        }
        if (!last.isEmpty()) {
            result.put("last", last);
        }
        return result;
    }

    public Collection<ItemDtoInfo> toItemDtoForOwner(List<Item> items, List<Booking> next, List<Booking> last,
                                                     Map<Long, List<CommentDto>> commentsItem) {
        if (commentsItem == null) {
            return new ArrayList<>();
        }
        Map<String, Map<Long, BookingDtoInfo>> booking = toBookingDtoInfo(next, last);
        Map<Long, BookingDtoInfo> nextBooking = booking.get("next");
        Map<Long, BookingDtoInfo> lastBooking = booking.get("last");
        return items.stream()
                .map(item -> {
                    BookingDtoInfo nextDto = null;
                    BookingDtoInfo lastDto = null;
                    List<CommentDto> commentDto = new ArrayList<>();
                    if (nextBooking != null && nextBooking.containsKey(item.getId())) {
                        nextDto = nextBooking.get(item.getId());
                    }
                    if (lastBooking != null && lastBooking.containsKey(item.getId())) {
                        lastDto = lastBooking.get(item.getId());
                    }
                    if (commentsItem.containsKey(item.getId())) {
                        commentDto = commentsItem.get(item.getId());
                    }
                    return itemMapper.toItemDtoInfo(item, nextDto, lastDto, commentDto);
                }).sorted(Comparator.comparing(ItemDtoInfo::getId))
                .collect(Collectors.toList());
    }
}
