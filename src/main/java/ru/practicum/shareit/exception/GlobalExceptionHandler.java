package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public GlobalErrorResponse handleUserNotFoundException(final UserNotFoundException exception) {
        log.warn("404 {}", exception.getMessage(), exception);
        return new GlobalErrorResponse(exception.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public GlobalErrorResponse handleItemNotFoundException(final ItemNotFoundException exception) {
        log.warn("404 {}", exception.getMessage(), exception);
        return new GlobalErrorResponse(exception.getMessage());
    }
}
