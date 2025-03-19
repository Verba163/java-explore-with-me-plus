package ru.practicum.ewm.client;

import ru.practicum.dto.StatHitDto;
import ru.practicum.dto.StatViewDto;

public interface StatClient {

    void hit(StatHitDto paramHitDto);

    StatViewDto getStat(ParamDto paramDto);
}
