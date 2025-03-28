package ru.practicum.ewm.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.ewm.error.exception.IllegalArgumentException;
import ru.practicum.ewm.error.exception.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleValidationExceptions(final ValidationException e) {
        log.warn("400 {}", e.getMessage(), e);

        return ApiError.builder()
                .message("BAD_REQUEST")
                .reason("Incorrectly made request.")
                .status(HttpStatus.BAD_REQUEST.toString())
                .errors(List.of(e.getMessage()))
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError MethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        log.warn("400 {}", e.getMessage(), e);

        List<String> errorMessages = e.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getDefaultMessage())
                .collect(Collectors.toList());

        return ApiError.builder()
                .message("BAD_REQUEST")
                .reason("MethodArgumentNotValidException")
                .status(HttpStatus.BAD_REQUEST.toString())
                .errors(errorMessages)
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler(AccessException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiError handleAccessException(final AccessException e) {
        log.warn("403 {}", e.getMessage(), e);

        return ApiError.builder()
                .message("FORBIDDEN")
                .reason("ACCESS DENIED")
                .status(HttpStatus.FORBIDDEN.toString())
                .errors(List.of(e.getMessage()))
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFoundException(final NotFoundException e) {
        log.warn("404 {}", e.getMessage(), e);
        return ApiError.builder()
                .message("NOT_FOUND")
                .reason("The required object was not found.")
                .status(HttpStatus.NOT_FOUND.toString())
                .errors(List.of(e.getMessage()))
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleDataIntegrityViolationException(final DataIntegrityViolationException e) {
        log.warn("409 {}", e.getMessage(), e);

        return ApiError.builder()
                .message("CONFLICT")
                .reason("Integrity constraint has been violated.")
                .status(HttpStatus.CONFLICT.toString())
                .errors(List.of(e.getMessage()))
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleIllegalArgumentException(final IllegalArgumentException e) {
        log.warn("400 {}", e.getMessage(), e);

        return ApiError.builder()
                .message("BAD_REQUEST")
                .reason("Incorrectly made request.")
                .status(HttpStatus.BAD_REQUEST.toString())
                .errors(List.of(e.getMessage()))
                .timestamp(LocalDateTime.now())
                .build();
    }
}