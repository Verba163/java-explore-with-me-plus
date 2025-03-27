package ru.practicum.ewm.user.service;

import ru.practicum.ewm.user.dto.NewUserRequestDto;
import ru.practicum.ewm.user.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto createUser(NewUserRequestDto newUserRequestDto);

    List<UserDto> getAllUsers(List<Long> ids, Integer from, Integer size);

    UserDto getUserById(Long userId);

    void deleteUser(Long userId);
}
