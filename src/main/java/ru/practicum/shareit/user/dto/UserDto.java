package ru.practicum.shareit.user.dto;

import lombok.*;

import javax.validation.constraints.*;

@Getter
@Setter
@AllArgsConstructor
@ToString
@Builder
public class UserDto {
    @Positive
    private Long id;
    @NotBlank
    @Size(max = 50)
    private String name;
    @NotEmpty
    @Email
    private String email;
}
