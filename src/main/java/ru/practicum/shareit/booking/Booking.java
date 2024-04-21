package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Setter
public class Booking {
    private Long id;
    private Long itemId;
    private Long bookerId;
    private String status;
    private LocalDateTime start;
    private LocalDateTime end;
}
