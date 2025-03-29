package ru.practicum.ewm.events.dto.parameters;

import lombok.Builder;
import lombok.Getter;
import ru.practicum.ewm.events.dto.EventRequestStatusUpdateRequest;

@Builder(toBuilder = true)
@Getter
public class UpdateRequestsStatusRequestParams {
    private Long userId;
    private Long eventId;
    private EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest;
}
