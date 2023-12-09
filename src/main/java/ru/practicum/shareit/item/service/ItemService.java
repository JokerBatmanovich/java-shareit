package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemToGetDto;
import ru.practicum.shareit.item.dto.ItemToReturnDto;

import java.util.List;

public interface ItemService {

    ItemToReturnDto getById(Long itemId, Long userId);

    List<ItemToReturnDto> getByOwnerId(Long userId, Integer from, Integer size);

    ItemToReturnDto add(ItemToGetDto itemToGetDto, Long userId);

    ItemToReturnDto update(ItemToGetDto itemToGetDto, Long itemId, Long userId);

    List<ItemToReturnDto> search(String text, Integer from, Integer size);
}
