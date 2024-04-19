package ru.practicum.shareit.booking;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Booking {
    private Long id;
    private Long itemId;
    private Long bookerId;
    private String status;
    private LocalDateTime start;
    private LocalDateTime end;
}
