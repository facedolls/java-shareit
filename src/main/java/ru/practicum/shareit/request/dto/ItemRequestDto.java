package ru.practicum.shareit.request.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class ItemRequestDto {
    @NotBlank
    private String description;
}
