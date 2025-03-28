package ru.practicum.ewm.error;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ApiError {

    String message;
    String reason;
    String status;
    List<String> errors;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime timestamp;

    public ApiError(String message, String reason, String status, List<String> errors, LocalDateTime timestamp) {
        this.message = message;
        this.reason = reason;
        this.status = status;
        this.errors = errors;
        this.timestamp = timestamp;
    }

    public ApiError() {
        this.timestamp = LocalDateTime.now();
    }
}
