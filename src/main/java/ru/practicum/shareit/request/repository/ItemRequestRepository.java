package ru.practicum.shareit.request.repository;

import ru.practicum.shareit.user.User;

public interface ItemRequestRepository {
    User getById(Long id);

    User add(User user);

    User update(User user);

    void delete(Long id);

}
