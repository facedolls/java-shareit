package ru.practicum.shareit.item.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.booking.dto.BookingDtoInfo;

import java.util.List;

@Getter
@Setter
@ToString
public class ItemDtoInfo {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private BookingDtoInfo lastBooking;
    private BookingDtoInfo nextBooking;
    private List<CommentDto> comments;
}
