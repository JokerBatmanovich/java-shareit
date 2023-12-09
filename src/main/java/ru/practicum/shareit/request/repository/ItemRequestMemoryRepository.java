package ru.practicum.shareit.request.repository;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;

@Component
public class ItemRequestMemoryRepository implements ItemRequestRepository {

    private final HashMap<Long, User> users = new HashMap<>();
    private Long idCounter = 1L;

    @Override
    public User getById(Long id) {
        if (users.containsKey(id)) {
            throw new ItemRequestNotFoundException(id);
        }
        return users.get(id);
    }

    @Override
    public User add(User user) {
        users.put(idCounter, user);
        return getById(idCounter++);
    }

    @Override
    public User update(User user) {
        getById(user.getId());
        users.put(user.getId(), user);
        return getById(user.getId());
    }

    @Override
    public void delete(Long id) {
        getById(id);
        users.remove(id);
    }

}
