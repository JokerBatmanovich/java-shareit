package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.EmailAlreadyExistException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.exception.UserNotFoundException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Repository
public class UserMemoryRepository implements UserRepository {
    private final Map<Long, User> users = new HashMap<>();
    private Long idCounter = 1L;

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User getById(Long id) {
        if (!users.containsKey(id)) {
            throw new UserNotFoundException(id);
        }
        return users.get(id);
    }

    @Override
    public User getByEmail(String email) {
        for (User user : users.values()) {
            if (user.getEmail().equalsIgnoreCase(email)) {
                return user;
            }
        }
        throw new UserNotFoundException("Пользователь с Email: " + email + " не найден.");
    }

    @Override
    public User add(User newUser) {
        users.values()
                .forEach(user -> {
                    if (user.getEmail().equalsIgnoreCase(newUser.getEmail())) {
                        throw new EmailAlreadyExistException(newUser.getEmail());
                    }
        });
        newUser.setId(idCounter);
        users.put(idCounter++, newUser);
        return newUser;// Я возвращал экземпляр из хранилища для полноценной проверки правильности добавленного объекта,
    }                  // чтобы полноценно убедиться в том, что объект добавился корректно.

    @Override
    public User update(User updUser) {
        User oldUser = getById(updUser.getId());
        /*
            В методе обновления я никак не смогу избавиться от обращения к БД. Мне нужно взять существующий объект,
            чтобы вконце вернуть его полную обновленную версию, т.к., согласно Postman-тестам, нам на обновление могут
            приходить отдельные поля (только имя или только e-mail).
        */
        users.values()
                .forEach(user -> {
                    if (user.getEmail().equalsIgnoreCase(updUser.getEmail()) && !updUser.getId().equals(user.getId())) {
                        throw new EmailAlreadyExistException(updUser.getEmail());
                    }
                });
        if (updUser.getName() != null) {
            users.get(updUser.getId()).setName(updUser.getName());
            oldUser.setName(updUser.getName());
        }
        if (updUser.getEmail() != null) {
            users.get(updUser.getId()).setEmail(updUser.getEmail());
            oldUser.setEmail(updUser.getEmail());
        }
        return oldUser;
    }


    @Override
    public void delete(Long id) {
        getById(id);
        users.remove(id);
    }

}
