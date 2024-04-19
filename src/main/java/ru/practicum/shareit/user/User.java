package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@Getter
@Setter
public class User {
    private Long id;
    @Email
    @NotBlank
    private String email;
    @NotBlank
    private String name;
}
