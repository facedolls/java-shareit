package ru.practicum.shareit.request;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Setter
public class ItemRequest {
    private Long id;
    private String description;
    private Long consumerId;
    private LocalDateTime created;
}
