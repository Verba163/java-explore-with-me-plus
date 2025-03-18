package ru.practicum.ewm.client;

import ru.practicum.dto.ParamDto;
import ru.practicum.dto.ParamHitDto;
import ru.practicum.dto.StatDto;

public interface StatClient {

    void hit(ParamHitDto paramHitDto);

    StatDto getStat(ParamDto paramDto);
}
