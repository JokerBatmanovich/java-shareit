package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    @Qualifier("itemMemoryRepository")
    final ItemRepository itemRepository;
    @Qualifier("userMemoryRepository")
    final UserRepository userRepository;

    @Override
    public Item get(Long itemId) {
        return itemRepository.getById(itemId);
    }

    @Override
    public List<Item> getAll() {
        return new ArrayList<>(itemRepository.getAll());
    }

    @Override
    public List<Item> getByUserId(Long userId) {
        return new ArrayList<>(itemRepository.getByUserId(userId));
    }

    @Override
    public Item add(Item item, Long userId) {
        item.setOwner(userRepository.getById(userId));
        return itemRepository.add(item);
    }

    @Override
    public Item update(Item item) {
        return itemRepository.update(item);
    }

    @Override
    public void deleteById(Long id) {
        itemRepository.deleteById(id);
    }

    @Override
    public void deleteByUserId(Long userId) {
        itemRepository.deleteByUserId(userId);
    }

    @Override
    public List<Item> search(String text) {
        return itemRepository.search(text);
    }

}
