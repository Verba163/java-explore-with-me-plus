package ru.practicum.ewm.events.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.events.enums.AdminEventAction;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateEventAdminRequest {
    private String title;
    private String description;
    private String annotation;
    private Long category;
    private LocationDto location;
    private Boolean requestModeration;
    private Boolean paid;
    private Integer participantLimit;
    private LocalDateTime eventDate;
    private AdminEventAction stateAction;
}