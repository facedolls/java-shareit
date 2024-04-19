package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@Data
public class UserDto {
    private Long id;
    @Email
    @NotBlank
    private String email;
    @NotBlank
    private String name;
}
