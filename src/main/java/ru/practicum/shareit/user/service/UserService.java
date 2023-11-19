package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.User;

import java.util.List;

public interface UserService {
    User getById(Long userId);

    User getByEmail(String email);

    List<User> getAll();

    User add(User user);

    User update(User user);

    void delete(Long userId);
}
