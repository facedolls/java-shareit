package ru.practicum.shareit.booking.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.booking.dto.validator.ValidBookingCreate;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@ValidBookingCreate
public class BookingDtoCreate {
    private Long itemId;
    private LocalDateTime start;
    private LocalDateTime end;
}
