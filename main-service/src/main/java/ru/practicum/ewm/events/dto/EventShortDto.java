package ru.practicum.ewm.events.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.user.dto.UserShortDto;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventShortDto {

    Long id;

    String annotation;

    String title;

    Long views;

    CategoryDto categoryDto;

    Long confirmedRequests;

    UserShortDto initiator;

    LocalDateTime eventDate;

    boolean paid;

}
