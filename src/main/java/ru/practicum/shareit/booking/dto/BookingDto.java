package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class BookingDto {
    private Long itemId;
    private Long bookerId;
    private String status;
    private LocalDateTime start;
    private LocalDateTime end;
}
