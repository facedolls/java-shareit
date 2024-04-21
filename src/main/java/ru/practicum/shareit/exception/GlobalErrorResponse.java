package ru.practicum.shareit.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
@Setter
public class GlobalErrorResponse {
    private final String message;
}
