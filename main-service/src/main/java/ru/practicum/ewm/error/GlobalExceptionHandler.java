package ru.practicum.ewm.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.practicum.ewm.error.exception.*;

import java.lang.IllegalArgumentException;
import java.time.LocalDateTime;
import java.util.Collections;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleValidationExceptions(final ValidationException e) {
        log.warn("400 {}", e.getMessage(), e);
        return new ApiError("BAD_REQUEST", "Incorrectly made request.",
                e.getMessage(), Collections.singletonList(e.getMessage()), LocalDateTime.now());
    }

    @ExceptionHandler(AccessException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiError handleAccessException(final AccessException e) {
        log.warn("403 {}", e.getMessage(), e);
        return new ApiError("FORBIDDEN", "ACCESS DENIED ", e.getMessage(),
                Collections.singletonList(e.getMessage()), LocalDateTime.now());
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFoundException(final NotFoundException e) {
        log.warn("404 {}", e.getMessage(), e);
        return new ApiError("NOT FOUND", "The required object was not found.",
                e.getMessage(), Collections.singletonList(e.getMessage()), LocalDateTime.now());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleDataIntegrityViolationException(final DataIntegrityViolationException e) {
        log.warn("409 {}", e.getMessage(), e);
        return new ApiError("CONFLICT", "Integrity constraint has been violated.",
                e.getMessage(), Collections.singletonList(e.getMessage()), LocalDateTime.now());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleIllegalArgumentException(final IllegalArgumentException e) {
        log.warn("400 {}", e.getMessage(), e);
        return new ApiError("BAD_REQUEST", "Incorrectly made request.",
                e.getMessage(), Collections.singletonList(e.getMessage()), LocalDateTime.now());
    }

}