package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingItemService {
    private final BookingRepository bookingRepository;

    public List<Booking> getNextBookingsForOwner(LocalDateTime current, List<Long> itemsId, BookingStatus status) {
        return bookingRepository.findNextBookingsForOwner(current, itemsId, status);
    }

    public List<Booking> getLastBookingsForOwner(LocalDateTime current, List<Long> itemsId, BookingStatus status) {
        return bookingRepository.findLastBookingsForOwner(current, itemsId, status);
    }

    public Boolean isExistsByItemIdAndBookerIdAndStatusAndEndBefore(Long itemId, Long userId, BookingStatus bookingStatus,
                                                                    LocalDateTime localDateTime) {
        return bookingRepository.existsByItemIdAndBookerIdAndStatusAndEndBefore(
                itemId, userId, bookingStatus, localDateTime);
    }
}
