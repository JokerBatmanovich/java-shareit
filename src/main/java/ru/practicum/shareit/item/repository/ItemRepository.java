package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {
    Item getById(Long id);

    List<Item> getAll();

    List<Item> getByUserId(Long userId);

    Item add(Item item);

    Item update(Item item);

    void deleteById(Long id);

    void deleteByUserId(Long userId);

    List<Item> search(String text);
}
