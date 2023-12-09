package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserToGetDto;
import ru.practicum.shareit.user.dto.UserToReturnDto;

import java.util.List;

public interface UserService {
    UserToReturnDto getById(Long userId);

    List<UserToReturnDto> getAll();

    UserToReturnDto add(UserToGetDto userToGetDto);

    UserToReturnDto update(UserToGetDto userToGetDto, Long userId);

    void delete(Long userId);
}
