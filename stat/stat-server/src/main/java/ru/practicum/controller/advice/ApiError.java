package ru.practicum.controller.advice;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public class ApiError {
    private final HttpStatus status;
    private final String title;
    private final String message;
    private final String stackTrace;
}
