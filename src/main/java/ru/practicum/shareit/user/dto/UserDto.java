package ru.practicum.shareit.user.dto;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;

@Getter
@Setter
@AllArgsConstructor
@ToString
@Builder
public class UserDto {
    @Positive
    private Long id;
    @NotBlank
    private String name;
    @NotEmpty
    @Email
    private String email;
}
