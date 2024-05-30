package ru.practicum.shareit.booking.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class BookingDtoCreate {
    private Long itemId;
    private LocalDateTime start;
    private LocalDateTime end;
}
