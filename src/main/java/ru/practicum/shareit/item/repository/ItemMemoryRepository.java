package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.*;

@Component
@Repository
public class ItemMemoryRepository implements ItemRepository {
    private final Map<Long, Item> items = new HashMap<>();
    private Long idCounter = 1L;

    @Override
    public Item getById(Long id) {
        if (!items.containsKey(id)) {
            throw new ItemNotFoundException(id);
        }
        return (items.get(id));
    }

    @Override
    public List<Item> getAll() {
        return new ArrayList<>(items.values());
    }

    @Override
    public List<Item> getByUserId(Long userId) {
        List<Item> listToReturn = new ArrayList<>();
        items.values()
                .forEach(item -> {
                    if (Objects.equals(item.getOwner().getId(), userId)) {
                        listToReturn.add(item);
                    }
                });
        return listToReturn;
    }

    @Override
    public Item add(Item item) {
        item.setId(idCounter);
        items.put(idCounter++, item);
        return item;
        /*
            Я возвращал экземпляр из хранилища для полноценной проверки правильности добавленного объекта,чтобы
            полноценно убедиться в том, что он добавился корректно.
        */
    }

    @Override
    public Item update(Item updItem) {
        Item item = getById(updItem.getId());
        /*
            В методе обновления я никак не смогу избавиться от обращения к БД. Мне нужно взять существующий объект,
            чтобы в конце вернуть его полную обновленную версию, т.к., согласно Postman-тестам, нам на обновление могут
            приходить отдельные поля (только имя или только e-mail).
        */
        if (updItem.getName() != null) {
            items.get(updItem.getId()).setName(updItem.getName());
            item.setName(updItem.getName());
        }
        if (updItem.getDescription() != null) {
            items.get(updItem.getId()).setDescription(updItem.getDescription());
            item.setDescription(updItem.getDescription());
        }
        if (updItem.getAvailable() != null) {
            items.get(updItem.getId()).setAvailable(updItem.getAvailable());
            item.setAvailable(updItem.getAvailable());
        }
        return item;
    }

    @Override
    public void deleteById(Long id) {
        getById(id);
        items.remove(id);
    }

    @Override
    public void deleteByUserId(Long userId) {
        List<Long> idsToRemove = new ArrayList<>();
        items.values()
                .forEach(item -> {
                    if (Objects.equals(item.getOwner().getId(), userId)) {
                        idsToRemove.add(item.getId());
                    }
                });
        idsToRemove.forEach(items::remove);
    }

    @Override
    public List<Item> search(String text) {
        List<Item> listToReturn = new ArrayList<>();
        if (!text.isBlank()) {
            items.values().forEach(item -> {
                if (item.getName().toLowerCase().contains(text.toLowerCase())
                        || item.getDescription().toLowerCase().contains(text.toLowerCase())
                        && item.getAvailable()) {
                    listToReturn.add(item);
                }
            });
        }
        return listToReturn;
    }

}
