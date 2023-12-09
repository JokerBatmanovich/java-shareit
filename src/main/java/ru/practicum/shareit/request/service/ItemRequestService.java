package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestToGetDto;
import ru.practicum.shareit.request.dto.ItemRequestToReturnDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestToReturnDto getById(Long requestId, Long userId);

    List<ItemRequestToReturnDto> getByOwner(Long userId);

    List<ItemRequestToReturnDto> getAll(Long userId, Integer from, Integer size);

    ItemRequestToReturnDto add(ItemRequestToGetDto itemRequestToGetDto, Long userId);

}
