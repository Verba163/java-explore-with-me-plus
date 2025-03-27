package ru.practicum.ewm.events.dto;

import lombok.Builder;
import lombok.Getter;

@Builder(toBuilder = true)
@Getter
public class UpdateRequestsStatusRequestParams {
    private Long userId;
    private Long eventId;
    private EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest;
}
