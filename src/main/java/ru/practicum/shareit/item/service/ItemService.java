package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item get(Long id);

    List<Item> getAll();

    List<Item> getByUserId(Long userId);

    Item add(Item item, Long userId);

    Item update(Item item);

    void deleteById(Long id);

    void deleteByUserId(Long userId);

    List<Item> search(String text);
}
