package ru.practicum.shareit.item.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
@ToString
@Builder
public class ItemDto {
    @Positive
    private Long id;
    @NotBlank
    @Size
    private String name;
    @NotBlank
    @Size
    private String description;
    @NotNull
    private Boolean available;
    private Long requestId;
}
