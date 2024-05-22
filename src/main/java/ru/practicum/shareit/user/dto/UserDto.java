package ru.practicum.shareit.user.dto;

import lombok.*;
import ru.practicum.shareit.validated.Create;
import ru.practicum.shareit.validated.Update;

import javax.validation.constraints.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class UserDto {
    @Positive(groups = Update.class)
    private Long id;
    @NotBlank(groups = Create.class)
    @Size(max = 50, groups = Create.class)
    private String name;
    @NotEmpty(groups = Create.class)
    @Email(groups = {Create.class, Update.class})
    private String email;
}
