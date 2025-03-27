package ru.practicum.ewm.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.events.storage.EventsRepository;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.mapper.RequestMapper;
import ru.practicum.ewm.request.model.Request;
import ru.practicum.ewm.request.repository.RequestRepository;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventsRepository eventsRepository;

    @Override
    public List<ParticipationRequestDto> getUserRequests(Long userId) {
        return requestRepository.findByRequesterId(userId).stream()
                .map(RequestMapper::toRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public ParticipationRequestDto createUserRequest(Long userId, Long eventId) {
        Event event = eventsRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException(""));
        User requester = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException(""));

        Request newRequest = RequestMapper.toRequestEntity(new ParticipationRequestDto(), event, requester);
        return RequestMapper.toRequestDto(requestRepository.save(newRequest));
    }

    @Override
    public ParticipationRequestDto cancelUserRequest(Long userId, Long requestId) {
        return requestRepository.findById(requestId)
                .map(RequestMapper::toRequestDto)
                .orElseThrow(() -> new RuntimeException(String.format("Request not found with id %d", requestId)));
    }
}
