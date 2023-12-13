package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.Update;
import ru.practicum.shareit.exception.InvalidIdException;

@RestController
@RequestMapping(path = "/users")
@Validated
@RequiredArgsConstructor
public class UserController {
    private final UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> getAll() {
        return userClient.getAll();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> get(@PathVariable Long userId) {
        return userClient.getUser(userId);
    }

    @PostMapping()
    public ResponseEntity<Object> add(@RequestBody @Validated({Create.class}) UserToGetDto userToGetDto) {
        return userClient.add(userToGetDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> update(@RequestBody @Validated({Update.class}) UserToGetDto userToGetDto,
                                         @PathVariable Long userId) {
        if (userToGetDto.getId() != null && !userToGetDto.getId().equals(userId)) {
            throw new InvalidIdException("ID тела запроса не совпадает с ID из параметров.");
        }
        userToGetDto.setId(userId);
        return userClient.update(userId, userToGetDto);
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable Long userId) {
        userClient.delete(userId);
    }

}
